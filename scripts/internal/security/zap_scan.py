"""ZAPコンテナ内で実行する。通信本文と資格情報は出力しない。"""

import html
from html.parser import HTMLParser
import json
import os
from pathlib import Path
import secrets
import subprocess
import sys
import time
from urllib.parse import urljoin, urlsplit
from collections import Counter

import requests
import yaml


BFF = "http://host.docker.internal:3000"
RESERVATION_PATH = "/search?isbn=9780000000003"
API = "http://host.docker.internal:8080"
ZAP = "http://127.0.0.1:8090"
AUTH = Path("/tmp/zap-auth.json")
LOST = Path("/tmp/zap-auth-lost")
DEADLINE = 0


class ScanFailure(Exception):
    pass


def check_time():
    if time.monotonic() >= DEADLINE:
        raise ScanFailure("時間上限・未完了")


def zap(component, operation, action, **parameters):
    check_time()
    response = requests.post(f"{ZAP}/JSON/{component}/{operation}/{action}/",
                             data=parameters, headers={"Content-Type": "application/x-www-form-urlencoded"},
                             timeout=30)
    result = response.json()
    if response.status_code != 200 or "code" in result:
        code = result.get("code", "http-error")
        raise ScanFailure(f"ZAP制御 {component}/{action} に失敗（{code}）")
    return result


def message(method, url, body=None, headers=None):
    """ZAP経由で送り、リダイレクトを自動追跡しない。"""
    check_time()
    parsed = urlsplit(url)
    if parsed.scheme != "http" or parsed.netloc not in (urlsplit(BFF).netloc, urlsplit(API).netloc):
        raise ScanFailure("対象外URLを拒否")
    payload = body or ""
    request_headers = {"Host": parsed.netloc, "Connection": "close", **(headers or {})}
    if body is not None:
        request_headers["Content-Length"] = str(len(payload.encode()))
    raw = f"{method} {url} HTTP/1.1\r\n"
    raw += "".join(f"{key}: {value}\r\n" for key, value in request_headers.items())
    raw += "\r\n" + payload
    results = zap("core", "action", "sendRequest", request=raw, followRedirects="false")["sendRequest"]
    if len(results) != 1:
        raise ScanFailure("通信結果が不正")
    result = results[0]
    header = result["responseHeader"]
    status = int(header.split()[1])
    return status, header, result["responseBody"]


class Forms(HTMLParser):
    def __init__(self, source):
        super().__init__(convert_charrefs=True)
        self.forms = []
        self.current = None
        self.feed(source)

    def handle_starttag(self, tag, attributes):
        attributes = dict(attributes)
        if tag == "form":
            self.current = {"action": attributes.get("action", ""), "fields": []}
            self.forms.append(self.current)
        if tag == "input" and self.current is not None and attributes.get("name"):
            self.current["fields"].append((attributes["name"], attributes.get("value", "")))

    def handle_endtag(self, tag):
        if tag == "form":
            self.current = None


def get_form(path, required):
    status, _, body = message("GET", BFF + path)
    if status != 200:
        raise ScanFailure("BFF画面に到達できない")
    matches = [form for form in Forms(body).forms if required in dict(form["fields"])]
    if len(matches) != 1:
        raise ScanFailure("対象フォームを特定できない")
    return matches[0]


def submit_form(path, form, values, origin=BFF):
    fields = [(key, values.get(key, value)) for key, value in form["fields"]]
    fields.extend((key, value) for key, value in values.items() if key not in dict(fields))
    url = urljoin(BFF + path, form["action"])
    boundary = "security-" + secrets.token_hex(16)
    body = "".join(f'--{boundary}\r\nContent-Disposition: form-data; name="{key}"\r\n\r\n{value}\r\n'
                   for key, value in fields) + f"--{boundary}--\r\n"
    return message("POST", url, body,
                   {"Content-Type": f"multipart/form-data; boundary={boundary}", "Origin": origin})


def save_auth(mode, cookie="", token="", email="", password=""):
    AUTH.write_text(json.dumps({"mode": mode, "cookie": cookie, "token": token,
                               "email": email, "password": password}))
    LOST.unlink(missing_ok=True)
    Path("/tmp/zap-auth-failed").unlink(missing_ok=True)
    Path("/tmp/zap-auth-error").unlink(missing_ok=True)


def authenticate(email, password):
    save_auth("anonymous")
    form = get_form("/login", "email")
    status, headers, _ = submit_form("/login", form, {"email": email, "password": password})
    if status != 303:
        raise ScanFailure("BFF認証失敗")
    cookies = [line.split(":", 1)[1].strip().split(";", 1)[0]
               for line in headers.splitlines() if line.lower().startswith("set-cookie: book_vista_session=")]
    if len(cookies) != 1:
        raise ScanFailure("BFFセッションを確認できない")
    status, _, body = message("POST", API + "/api/v1/auth/login",
                              json.dumps({"email": email, "password": password}),
                              {"Content-Type": "application/json"})
    result = json.loads(body)
    if status != 200 or result.get("token_type") != "Bearer" or not result.get("access_token"):
        raise ScanFailure("API認証失敗")
    save_auth("authenticated", cookies[0], result["access_token"], email, password)
    verify(True)


def verify(authenticated):
    status, _, body = message("GET", BFF + "/search?isbn=9780000000001")
    verify_sender()
    if status != 200 or (("ログアウトする" in body) != authenticated):
        raise ScanFailure("BFF認証状態を確認できない")
    if authenticated and "世界史入門" not in html.unescape(body):
        raise ScanFailure("BFF検索結果へ到達できない")
    status, _, body = message("GET", API + "/api/v1/book-items?isbn=9780000000001")
    if status != (200 if authenticated else 401):
        raise ScanFailure("API認証状態を確認できない")
    if authenticated and not json.loads(body).get("book_items"):
        raise ScanFailure("API検索結果へ到達できない")


def wait_scan(component, scan_id):
    while int(zap(component, "view", "status", scanId=scan_id)["status"]) < 100:
        check_time()
        time.sleep(0.5)


def passive_wait():
    while int(zap("pscan", "view", "recordsToScan")["recordsToScan"]):
        check_time()
        time.sleep(0.5)


def setup_zap():
    save_auth("anonymous")
    expected = {"graaljs": "0.14.0", "network": "0.33.0", "ascanrules": "84.0.0",
                "scripts": "45.20.0", "openapi": "58.0.0", "automation": "0.62.0",
                "pscanrules": "77.0.0"}
    installed = {addon["id"]: addon["version"] for addon in
                 zap("autoupdate", "view", "installedAddons")["installedAddons"]}
    if any(installed.get(name) != version for name, version in expected.items()):
        raise ScanFailure("固定したアドオン版と一致しない")
    zap("script", "action", "load", scriptName="security-sender", scriptType="httpsender",
        scriptEngine="ECMAScript : Graal.js", fileName="/checks/zap_sender.js")
    zap("script", "action", "enable", scriptName="security-sender")
    # Frameworkで標準ルールのポリシーと直列実行を定義する。
    plan = {"env": {"contexts": [
                {"name": "BFF", "urls": [BFF], "includePaths": [r"http://host\.docker\.internal:3000/(|register|login|search)(\?.*)?"]},
                {"name": "API", "urls": [API], "includePaths": [r"http://host\.docker\.internal:8080/api/v1/.*"]}]},
            "jobs": [{"type": "activeScan-config", "parameters": {"threadPerHost": 1,
                       "hostPerScan": 1, "handleAntiCSRFTokens": False}},
                     {"type": "activeScan-policy", "parameters": {"name": "security-stable"},
                      "policyDefinition": {"defaultStrength": "LOW", "defaultThreshold": "MEDIUM"}}]}
    Path("/tmp/security-plan.yaml").write_text(yaml.safe_dump(plan))
    plan_id = zap("automation", "action", "runPlan", filePath="/tmp/security-plan.yaml")["planId"]
    while True:
        progress = zap("automation", "view", "planProgress", planId=plan_id)
        if progress.get("errors"):
            raise ScanFailure("Automation Framework設定失敗")
        if progress.get("finished"):
            break
        check_time()
        time.sleep(0.5)
    zap("spider", "action", "setOptionThreadCount", Integer=1)
    zap("spider", "action", "setOptionMaxDepth", Integer=3)
    zap("spider", "action", "setOptionProcessForm", Boolean="false")
    # 署名済みのAction参照・バインド情報は保持し、利用者が入力する項目を検査する。
    zap("ascan", "action", "addExcludedParam", name=r"\$ACTION.*", url=BFF + "/*")
    scanners = zap("ascan", "view", "scanners", scanPolicyName="security-stable")["scanners"]
    # ブラウザ検査は既存E2E、管理系と外部コールバックは初期対象外。
    excluded = {"40026", "40042", "40043", "90023"}
    excluded.update(scanner["id"] for scanner in scanners if scanner.get("quality") != "release")
    zap("ascan", "action", "disableScanners", ids=",".join(sorted(excluded)),
        scanPolicyName="security-stable")
    passive = zap("pscan", "view", "scanners")["scanners"]
    excluded_passive = [scanner["id"] for scanner in passive if scanner.get("quality") != "release"]
    if excluded_passive:
        zap("pscan", "action", "disableScanners", ids=",".join(excluded_passive))


def verify_sender():
    scripts = zap("script", "view", "listScripts")["listScripts"]
    sender = next((script for script in scripts if script["name"] == "security-sender"), {})
    if sender.get("enabled") != "true" or sender.get("error") != "false":
        raise ScanFailure("対象制限・認証スクリプトが正常に動作していない")
    if Path("/tmp/zap-auth-failed").exists():
        diagnostic = Path("/tmp/zap-auth-error")
        detail = diagnostic.read_text() if diagnostic.exists() else "認証応答不正"
        raise ScanFailure("検査中の認証維持に失敗（" + detail + "）")


def active(target):
    try:
        context_id = zap("context", "view", "context", contextName=target)["context"]["id"]
        # Contextから実際のContent-Typeと本文を保持した全メソッドのノードを選ぶ。
        scan_id = zap("ascan", "action", "scan", recurse="true", contextId=context_id,
                      inScopeOnly="true", scanPolicyName="security-stable")["scan"]
    except ScanFailure as error:
        raise ScanFailure(f"能動検査 {target}: {error}") from error
    wait_scan("ascan", scan_id)
    scans = zap("ascan", "view", "scans")["scans"]
    scan = next((item for item in scans if item["id"] == scan_id), {})
    if scan.get("state") != "FINISHED" or int(scan.get("reqCount", "0")) <= 0:
        raise ScanFailure("能動検査の実施・完了を確認できない")
    verify_sender()
    passive_wait()


def main():
    global DEADLINE
    os.umask(0o077)
    started = time.monotonic()
    DEADLINE = started + min(1800, float(os.environ.get("SECURITY_SCAN_SECONDS", "1800")))
    log = open("/tmp/zap-process.log", "w")
    process = subprocess.Popen(["/zap/zap.sh", "-daemon", "-host", "127.0.0.1", "-port", "8090",
                                "-dir", "/tmp/zap-home", "-config", "api.disablekey=true",
                                "-config", "start.checkForUpdates=false",
                                "-config", "start.checkAddonUpdates=false",
                                "-config", "start.installAddonUpdates=false",
                                "-config", "start.installScannerRules=false",
                                "-config", "connection.timeoutInSecs=10"], stdout=log, stderr=log)
    findings = False
    try:
        for _ in range(180):
            check_time()
            if process.poll() is not None:
                raise ScanFailure("ZAP起動失敗")
            try:
                if requests.get(ZAP + "/JSON/core/view/version/", timeout=1).status_code == 200:
                    break
            except requests.RequestException:
                pass
            time.sleep(0.5)
        else:
            raise ScanFailure("ZAP起動未完了")
        setup_zap()
        email = f"zap-{secrets.token_hex(12)}@example.com"
        password = secrets.token_urlsafe(24) + "Aa1!"
        registration = get_form("/register", "email")
        status, _, _ = submit_form("/register", registration, {"email": email, "password": password},
                                    origin="http://invalid.example")
        if status == 303:
            raise ScanFailure("不正Originが受理された")
        status, _, _ = submit_form("/register", registration, {"email": email, "password": password})
        if status != 303:
            raise ScanFailure(f"検査用ユーザーの登録失敗（HTTP {status}）")
        print("ZAP: 登録・Origin拒否を確認", flush=True)
        authenticate(email, password)
        reservation_form = get_form(RESERVATION_PATH, "bookProductId")
        save_auth("anonymous")
        specification_status, _, specification_body = direct_openapi()
        if specification_status != 200:
            raise ScanFailure("OpenAPI取得失敗")
        specification = json.loads(specification_body)
        sanitize_openapi(specification)
        specification["servers"] = [{"url": API}]
        specification["paths"] = {path: value for path, value in specification["paths"].items()
                                   if path.startswith("/api/v1/")}
        if not specification["paths"]:
            raise ScanFailure("業務APIが空")
        Path("/tmp/openapi.json").write_text(json.dumps(specification))
        for authenticated in (False, True):
            if authenticated:
                authenticate(email, password)
            else:
                save_auth("anonymous")
            verify(authenticated)
            for target, base in (("BFF", BFF), ("API", API)):
                zap("core", "action", "deleteAllAlerts")
                if target == "BFF":
                    for path in ("/", "/register", "/login", "/search", "/search?isbn=9780000000001"):
                        message("GET", BFF + path)
                    if authenticated:
                        reservation = get_form(RESERVATION_PATH, "bookProductId")
                        status, _, body = submit_form(RESERVATION_PATH, reservation, {})
                        if status != 200 or not any(text in body for text in
                                                  ("予約を受け付けました", "この本はすでに予約しています")):
                            raise ScanFailure("BFF予約操作へ到達できない")
                    else:
                        status, _, body = submit_form(RESERVATION_PATH, reservation_form, {})
                        if status != 200 or "ログインが必要です" not in body:
                            raise ScanFailure("BFF未認証の予約拒否を確認できない")
                    spider_id = zap("spider", "action", "scan", url=BFF, contextName="BFF",
                                    subtreeOnly="true")["scan"]
                    wait_scan("spider", spider_id)
                    urls = [(BFF + path, "GET") for path in ("/", "/register", "/login", "/search")]
                    urls += [(BFF + path, "POST") for path in ("/register", "/login", RESERVATION_PATH)]
                else:
                    zap("openapi", "action", "importFile", file="/tmp/openapi.json", target=API,
                        contextId=zap("context", "view", "context", contextName="API")["context"]["id"])
                    urls = [(API + path, method.upper()) for path, definition in specification["paths"].items()
                            for method in definition if method in ("get", "post", "put", "patch", "delete")]
                passive_wait()
                for url, method in urls:
                    print(f"ZAP: {target} {'認証済み' if authenticated else '未認証'} {method} {urlsplit(url).path} 検査開始", flush=True)
                for attempt in range(2):
                    LOST.unlink(missing_ok=True)
                    try:
                        active(target)
                        verify(authenticated)
                        if authenticated and LOST.exists():
                            raise ScanFailure("検査中の認証失効")
                        break
                    except ScanFailure:
                        if not authenticated or attempt:
                            raise
                        authenticate(email, password)
                if Path("/tmp/zap-blocked").exists():
                    print("ZAP: 対象外URLへの要求を遮断（検査対象外）", flush=True)
                alerts = zap("core", "view", "alerts", baseurl=base, start=0, count=10000)["alerts"]
                known_paths = {urlsplit(url).path for url, _ in urls}
                summary = Counter((alert["pluginId"], alert["risk"],
                                   urlsplit(alert["url"]).path if urlsplit(alert["url"]).path in known_paths
                                   else "<変形パス>") for alert in alerts)
                for (rule, risk, route), count in sorted(summary.items()):
                    if risk != "Informational":
                        findings = True
                    print(f"ZAP: {target} route={route} rule={rule} risk={risk} count={count}", flush=True)
                print(f"ZAP: {target} {'認証済み' if authenticated else '未認証'} 検査完了 alerts={len(alerts)}", flush=True)
        print(f"ZAP: 全4区分の検査完了 duration_seconds={time.monotonic() - started:.1f}", flush=True)
        return 10 if findings else 0
    except (ScanFailure, requests.RequestException, ValueError, KeyError, OSError) as error:
        print("ZAP: " + (str(error) if isinstance(error, ScanFailure) else "検査失敗・未完了"), flush=True)
        return 2
    finally:
        process.terminate()
        try:
            process.wait(timeout=10)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait()
        log.close()


def direct_openapi():
    # 定義取得は検査対象へ登録しない。外部参照は後続処理で拒否する。
    response = requests.get(API + "/v3/api-docs", allow_redirects=False, timeout=10)
    return response.status_code, "", response.text


def sanitize_openapi(value):
    if isinstance(value, dict):
        reference = value.get("$ref")
        if reference is not None and (not isinstance(reference, str) or not reference.startswith("#/")):
            raise ScanFailure("外部OpenAPI参照を拒否")
        # operation単位のserversによる接続先の上書きも防止する。
        value.pop("servers", None)
        for child in value.values():
            sanitize_openapi(child)
    elif isinstance(value, list):
        for child in value:
            sanitize_openapi(child)


if __name__ == "__main__":
    sys.exit(main())
