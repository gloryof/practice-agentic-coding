"""所有確認済みの検査環境だけで既存E2Eを実行する。"""

import os
import json
import re
import signal
import subprocess
import tempfile
import time

from security_environment import ROOT, WORK, EnvironmentFailure, say


def run_scenarios(deadline):
    commands = [
        ("API認証・失効・検索", [str(ROOT / "api/gradlew"), "-p", str(ROOT / "api"),
                             "e2eTest", "--rerun-tasks", "--tests", "*.LoginE2ETest",
                             "--tests", "*.LogoutE2ETest", "--tests", "*.BookItemSearchE2ETest",
                             "--console=plain"], ROOT),
        ("ブラウザ認証・予約・ヘッダー", ["node", "node_modules/@playwright/test/cli.js", "test",
                                      "--config=playwright.security.config.ts"], ROOT / "frontend"),
    ]
    result = 0
    with tempfile.TemporaryDirectory(prefix="security-scenarios-") as temporary:
        for label, arguments, cwd in commands:
            remaining = deadline - time.monotonic()
            if remaining <= 0:
                raise EnvironmentFailure("シナリオ検査が30分の時間上限に達しました。")
            if label.startswith("ブラウザ"):
                arguments += ["--output", temporary]
            # 失敗時も資格情報や通信本文を端末へ転送しない。
            browser = label.startswith("ブラウザ")
            process = subprocess.Popen(arguments, cwd=cwd, stdout=subprocess.PIPE if browser else subprocess.DEVNULL,
                                       stderr=subprocess.DEVNULL, start_new_session=True,
                                       env={**os.environ, "API_BASE_URL": "http://127.0.0.1:8080"})
            try:
                output, _ = process.communicate(timeout=remaining)
                code = process.returncode
                if browser:
                    code = max(code, browser_report_result(output))
                if code:
                    result = 2
                say(f"シナリオ {label}: {'完了' if code == 0 else '失敗・未完了'}")
            finally:
                # 自分が起動し、まだwaitで回収していないプロセス群だけを終了する。
                if process.poll() is None:
                    os.killpg(process.pid, signal.SIGTERM)
                    try:
                        process.wait(timeout=10)
                    except subprocess.TimeoutExpired:
                        os.killpg(process.pid, signal.SIGKILL)
                        process.wait()
    return result


def browser_report_result(output):
    try:
        report = json.loads(output)
    except (ValueError, TypeError):
        say("ブラウザ検査の結果を解析できませんでした。生の出力は表示しません。")
        return 2
    def visit(suite):
        for spec in suite.get("specs", []):
            if not spec.get("ok"):
                # 値・エラー本文ではなく、固定のテスト名だけを表示する。
                say(f"失敗したシナリオ: {spec['title']}")
        for child in suite.get("suites", []):
            visit(child)
    visit(report)
    stats = report.get("stats", {})
    if (report.get("errors") or stats.get("expected", 0) < 10 or
            any(stats.get(key, 0) for key in ("unexpected", "skipped", "flaky"))):
        say("ブラウザ検査: 必須10シナリオの完了条件を満たしません。")
        return 2
    return 0


def check_runtime_logs(run_id):
    # 値は表示せず、既知の合成資格情報と代表的な認証秘密の形式だけを検出する。
    pattern = re.compile(r"@example\.com|ValidPassword1!|Passw0rd!123456|Bearer [A-Za-z0-9_-]{20,}|book_vista_session=[A-Za-z0-9_-]{43}")
    for name in ("api", "bff"):
        log = WORK / run_id / f"{name}.log"
        if not log.is_file():
            say(f"{name}ログの確認は未完了です。")
            return 2
        if pattern.search(log.read_text(errors="replace")):
            say(f"{name}ログに秘密情報・個人情報の候補があります（値は非表示）。")
            return 1
    say("実行時ログ: 既知の合成資格情報・代表的な秘密情報形式の候補なし")
    return 0
