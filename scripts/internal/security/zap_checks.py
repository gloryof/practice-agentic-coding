"""検査環境とZAPの実行時間・所有権・終了判定を管理する。"""

import json
import os
import signal
import subprocess
import sys
import time
import threading
import uuid

from security_environment import ROOT, STATE, COMPOSE, EnvironmentFailure, say


def stream_scan_output(stream):
    # Composeやツールの生エラーを転送せず、検査側が整形した結果だけを表示する。
    for line in stream:
        if line.startswith("ZAP: "):
            print(line, end="", flush=True)


def main():
    if len(sys.argv) != 1:
        say("ZAP個別検査は引数を受け付けません。")
        return 2
    supervisor = None
    scanner = None
    result = 2
    run_id = None
    name = "security-zap-" + uuid.uuid4().hex
    def interrupted(_signum, _frame):
        raise InterruptedError()
    signal.signal(signal.SIGINT, interrupted)
    signal.signal(signal.SIGTERM, interrupted)
    try:
        # 子プロセスが残した管理情報を、自分の実行と取り違えない。
        if STATE.exists():
            raise EnvironmentFailure("既存の検査環境は引き継ぎません。先に所有ランナーから停止してください。")
        supervisor = subprocess.Popen([str(ROOT / ".codex/skills/run-api-e2e/scripts/run.sh"),
                                       "start", "--env", "security"], start_new_session=True)
        start = time.monotonic()
        while time.monotonic() - start < 1800:
            if supervisor.poll() is not None:
                raise EnvironmentFailure("検査環境を起動できませんでした。")
            if STATE.exists():
                state = json.loads(STATE.read_text())
                if state["pid"] != supervisor.pid:
                    raise EnvironmentFailure("検査環境の所有プロセスが一致しません。")
                run_id = state["run_id"]
                if state["ready"]:
                    break
            time.sleep(0.5)
        else:
            raise EnvironmentFailure("検査環境の準備が時間上限に達しました。")
        scanner = subprocess.Popen(COMPOSE + ["run", "--rm", "--no-deps", "-T", "--name", name, "zap"],
                                   env={**os.environ, "SECURITY_RUN_ID": run_id,
                                        "SECURITY_SCAN_SECONDS": "1800"}, start_new_session=True,
                                   stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, errors="replace")
        reader = threading.Thread(target=stream_scan_output, args=(scanner.stdout,), daemon=True)
        reader.start()
        try:
            # 結果読み取りと終了処理を含め、ZAPをホスト側でも監督する。
            code = scanner.wait(timeout=1800)
            reader.join(timeout=5)
        except subprocess.TimeoutExpired as error:
            raise EnvironmentFailure("ZAPは30分の時間上限に達しました。") from error
        result = {0: 0, 10: 1}.get(code, 2)
        if reader.is_alive():
            result = 2
        if result == 2:
            say("ZAPコンテナの起動・実行は失敗または未完了です。")
    except (EnvironmentFailure, OSError, ValueError, KeyError, InterruptedError) as error:
        say(str(error) if isinstance(error, EnvironmentFailure) else "ZAP検査は失敗・中断しました。")
    finally:
        signal.signal(signal.SIGINT, signal.SIG_IGN)
        signal.signal(signal.SIGTERM, signal.SIG_IGN)
        if scanner and scanner.poll() is None:
            # 名前だけでは削除せず、実行IDのラベルも照合する。
            inspected = subprocess.run(["docker", "inspect", name], capture_output=True, text=True)
            if inspected.returncode == 0:
                details = json.loads(inspected.stdout)[0]
                if details.get("Config", {}).get("Labels", {}).get("security.run") == run_id:
                    removed = subprocess.run(["docker", "rm", "-f", name], capture_output=True)
                    if removed.returncode:
                        result = 2
            scanner.terminate()
            try:
                scanner.wait(timeout=10)
            except subprocess.TimeoutExpired:
                scanner.kill()
                scanner.wait()
        if supervisor and supervisor.poll() is None:
            supervisor.terminate()
            try:
                code = supervisor.wait(timeout=90)
                if code:
                    result = 2
            except subprocess.TimeoutExpired:
                # 監督プロセスを強制終了して資源の管理情報を失わせない。
                say("環境の片付けが未完了です。監督プロセスと管理情報を残します。")
                result = 2
        elif supervisor and supervisor.returncode:
            result = 2
        if run_id and STATE.exists():
            result = 2
    say(f"ZAP判定: 終了コード{result}（0=指摘なし、1=指摘あり、2=失敗・未完了）")
    return result


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception:
        say("検査制御に失敗しました（未完了）。")
        sys.exit(2)
