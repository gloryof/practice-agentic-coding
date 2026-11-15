"""署名検証後に限定した入力を検査し、秘密値を出力せず0/1/2で報告する。"""

import json
import os
from pathlib import Path
import re
import shutil
import stat
import subprocess
import sys
import tempfile
import uuid


ROOT = Path(__file__).resolve().parents[3]
COMPOSE = ["docker", "compose", "--project-directory", str(ROOT),
           "--project-name", "security-check", "--file", str(ROOT / "compose.security.yml")]


class Incomplete(Exception):
    """失敗・検査対象外を指摘なしと区別する。"""


def run(arguments, *, env=None, timeout=900):
    try:
        result = subprocess.run(arguments, env=env, capture_output=True, timeout=timeout)
    except subprocess.TimeoutExpired as error:
        raise Incomplete("時間上限に到達") from error
    except OSError as error:
        raise Incomplete("コマンドを起動できない") from error
    if result.returncode:
        # 生の出力には秘密値や通信情報が含まれ得るため、再表示しない。
        diagnostic = result.stderr.decode("utf-8", errors="replace").lower()
        category = "起動・実行エラー"
        for words, label in [(("no space left",), "一時領域・キャッシュの容量不足"),
                             (("unauthorized", "authentication required", "denied"), "認証・権限エラー"),
                             (("timeout", "deadline exceeded"), "通信時間上限"),
                             (("download", "database"), "情報取得エラー"),
                             (("connect", "network", "resolve host"), "接続エラー")]:
            if any(word in diagnostic for word in words):
                category = label
                break
        raise Incomplete(f"{category}（{Path(arguments[0]).name}終了コード{result.returncode}）")
    return result.stdout


def parse_json(raw):
    try:
        return json.loads(raw)
    except (ValueError, UnicodeError) as error:
        raise Incomplete("検査結果が不正なJSON") from error


def compose_config():
    return parse_json(run(COMPOSE + ["config", "--format", "json"]))


def compose_run(service, arguments, *, env=None, timeout=900):
    name = "security-" + service + "-" + uuid.uuid4().hex
    try:
        return run(COMPOSE + ["run", "--rm", "--no-deps", "-T", "--name", name,
                              service, *arguments], env=env, timeout=timeout)
    except (Incomplete, KeyboardInterrupt):
        # 時間上限・中断でComposeクライアントが消えても、自分のコンテナだけを終了する。
        try:
            subprocess.run(["docker", "rm", "--force", name], capture_output=True, timeout=30)
        except (OSError, subprocess.TimeoutExpired):
            print("所有コンテナの後始末を確認してください:", name)
        raise


def verify(config):
    image = config["services"]["trivy"]["image"]
    match = re.fullmatch(r"ghcr\.io/aquasecurity/trivy:(\d+\.\d+\.\d+)@(sha256:[0-9a-f]{64})", image)
    if not match:
        raise Incomplete("Trivyの版・公式配布元・digest固定を確認できない")
    version, digest = match.groups()
    identity = r"^https://github\.com/aquasecurity/trivy/\.github/workflows/.+@refs/tags/v" + re.escape(version) + "$"
    compose_run("cosign", ["verify",
                   "--certificate-identity-regexp", identity,
                   "--certificate-oidc-issuer", "https://token.actions.githubusercontent.com",
                   "ghcr.io/aquasecurity/trivy@" + digest], timeout=180)
    print(f"Trivy {version}: 署名・発行元・透明性ログの検証に成功", flush=True)


def scan(input_directory, target, scanner):
    env = {**os.environ, "SECURITY_INPUT": str(input_directory)}
    command = [*target,
                        "--scanners", scanner, "--format", "json", "--quiet",
                        "--timeout", "10m", "--exit-code", "0", "--ignorefile", "/dev/null",
                        "--config", "", "--disable-telemetry", "--skip-version-check"]
    if scanner == "vuln":
        command += ["--list-all-pkgs", "--detection-priority", "comprehensive"]
    else:
        command += ["--cache-backend", "memory", "--secret-config", ""]
    report = parse_json(compose_run("trivy", command, env=env, timeout=720))
    if not isinstance(report, dict) or report.get("SchemaVersion") != 2:
        raise Incomplete("検査結果の形式が未対応")
    results = report.get("Results", [])
    if results is None:
        results = []
    if not isinstance(results, list):
        raise Incomplete("検査結果の一覧が不正")
    for result in results:
        if not isinstance(result, dict) or not isinstance(result.get("Target"), str):
            raise Incomplete("検査対象の結果が不正")
        for key in ("Vulnerabilities", "Secrets", "Packages"):
            items = result.get(key)
            if items is not None and (not isinstance(items, list) or any(not isinstance(item, dict) for item in items)):
                raise Incomplete("検査項目の結果が不正")
    return report, results


def safe(value):
    # 制御文字による端末表示の改ざんを避ける。
    return json.dumps(str(value), ensure_ascii=True)


def summarize(results, *, secrets=False):
    findings = 0
    for result in results:
        for finding in result.get("Secrets" if secrets else "Vulnerabilities") or []:
            if secrets:
                print("秘密情報候補:", safe(result["Target"]), safe(finding["RuleID"]),
                      "行", safe(finding.get("StartLine")), "（値・本文は非表示）")
            else:
                print("脆弱性:", safe(finding.get("PkgPath") or result["Target"]), safe(finding["VulnerabilityID"]),
                      safe(finding["PkgName"]), safe(finding["InstalledVersion"]),
                      safe(finding["Severity"]), "修正版", safe(finding.get("FixedVersion") or "なし"))
            findings += 1
    print(f"指摘件数: {findings}")
    return int(findings > 0)


def stage_tracked_files(destination):
    paths = run(["git", "-C", str(ROOT), "ls-files", "-z", "--cached"]).split(b"\0")
    count = 0
    for raw in paths:
        if not raw:
            continue
        relative = Path(os.fsdecode(raw))
        source = ROOT / relative
        # 中間ディレクトリのsymlinkも追跡しない。
        if relative.is_absolute() or ".." in relative.parts:
            raise Incomplete("Git管理対象のパスが不正")
        parents = [source, *list(source.parents)[:len(relative.parts) - 1]]
        if any(path.is_symlink() for path in parents):
            raise Incomplete(f"symlinkを含むため未検査: {safe(relative)}")
        try:
            mode = source.lstat().st_mode
        except FileNotFoundError:
            # 作業ツリーで削除済みのファイルは現在の対象に含めない。
            continue
        if not stat.S_ISREG(mode):
            raise Incomplete(f"通常ファイル以外は未検査: {safe(relative)}")
        output = destination / relative
        output.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, output)
        count += 1
    if not count:
        raise Incomplete("秘密情報検査の対象が空")
    print(f"現在のGit管理対象: {count}ファイル（未追跡・履歴は対象外）", flush=True)


def check_java_coverage(directory, results):
    jars = {path.relative_to(directory).as_posix() for path in directory.rglob("*.jar")}
    if not jars:
        raise Incomplete("依存JARが空")
    recognized = set()
    for result in results:
        if result.get("Type") != "jar" or not result.get("Packages"):
            continue
        for package in result["Packages"]:
            path = package.get("FilePath", "").removeprefix("/input/")
            if path in jars:
                recognized.add(path)
        target = result.get("Target", "").removeprefix("/input/")
        if target in jars:
            recognized.add(target)
    missing = sorted(jars - recognized)
    print(f"Java依存JAR: {len(jars)}件中{len(recognized)}件を識別")
    if missing:
        for path in missing[:50]:
            print("識別不能・未完了:", safe(path))
        if len(missing) > 50:
            print(f"ほか{len(missing) - 50}件が未識別（表示上限50件）")
        return 2
    return 0


def java_dependencies():
    print("Gradleで用途別の依存JARを同期中（コンパイルなし）", flush=True)
    run([str(ROOT / "api/gradlew"), "-p", str(ROOT / "api"),
         "syncSecurityDependencies", "--console=plain"], timeout=900)
    directory = ROOT / "api/build/security-dependencies"
    # fsモードはビルド前のファイルが対象で、JAR解析はrootfsモードで有効になる。
    _, results = scan(directory, ["rootfs", "/input"], "vuln")
    status = summarize(results)
    print("対象外: Gradle本体・JDK・ビルドプラグインは公式情報で別途確認する")
    return max(status, check_java_coverage(directory, results))


def container_images(empty_directory):
    status = 0
    for file, service in [("api/docker-compose.yml", "postgres"),
                          ("frontend/compose.redis.yml", "registration-redis")]:
        try:
            config = parse_json(run(["docker", "compose", "-f", str(ROOT / file),
                                     "config", "--format", "json"]))
            image = config["services"][service]["image"]
            print("コンテナ検査:", safe(image), flush=True)
            report, results = scan(empty_directory, ["image", "--image-src", "remote", image], "vuln")
            status = max(status, summarize(results))
            metadata = report.get("Metadata", {})
            print("検査digest:", safe(metadata.get("RepoDigests", [])))
            if not metadata.get("OS") or not any(r.get("Class") == "os-pkgs" and r.get("Packages") for r in results):
                raise Incomplete("OSパッケージの識別結果がない")
            if metadata["OS"].get("EOSL"):
                raise Incomplete("OSのサポート終了により未確認範囲がある")
        except (Incomplete, KeyError, TypeError) as error:
            print(f"{service}: 失敗・未完了（{error}）")
            status = 2
    return status


def main():
    if len(sys.argv) != 2 or sys.argv[1] not in {"verify", "java-dependencies", "container-images", "secrets"}:
        print("使用方法: trivy_checks.py <verify|java-dependencies|container-images|secrets>")
        return 2
    mode = sys.argv[1]
    try:
        verify(compose_config())
        if mode == "verify":
            return 0
        if mode == "java-dependencies":
            return java_dependencies()
        with tempfile.TemporaryDirectory(prefix="security-input-") as temporary:
            directory = Path(temporary)
            if mode == "container-images":
                return container_images(directory)
            stage_tracked_files(directory)
            _, results = scan(directory, ["fs", "--skip-dirs", "/input/.git", "/input"], "secret")
            return summarize(results, secrets=True)
    except Incomplete as error:
        print(f"{mode}: 失敗・未完了（{error}）")
        return 2
    except (KeyError, TypeError, ValueError, OSError, AttributeError, IndexError):
        # 不正なJSONの値や例外本文にも秘密値を含め得るため出力しない。
        print(f"{mode}: 失敗・未完了（署名検証・準備・通信・実行または結果形式を確認してください）")
        return 2
    except KeyboardInterrupt:
        print(f"{mode}: 中断・未完了")
        return 2


if __name__ == "__main__":
    sys.exit(main())
