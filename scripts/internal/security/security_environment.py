"""検査専用環境の監督。開発用ComposeとDB操作は呼び出さない。"""

import argparse
import fcntl
import json
import os
from pathlib import Path
import secrets
import shutil
import signal
import socket
import subprocess
import sys
import time
import urllib.request
import uuid


ROOT = Path(__file__).resolve().parents[3]
WORK = ROOT / "work/security"
STATE = WORK / "environment.json"
PORTS = (3000, 8080, 5432, 6379)
COMPOSE = ["docker", "compose", "--project-directory", str(ROOT), "--project-name",
           "security-check", "--file", str(ROOT / "compose.security.yml")]


class EnvironmentFailure(Exception):
    pass


def say(message):
    print(f"[security] {message}", flush=True)


def identity(pid):
    result = subprocess.run(["ps", "-p", str(pid), "-o", "lstart="],
                            capture_output=True, text=True)
    return result.stdout.strip() if result.returncode == 0 else None


def ports_available():
    # IPv4/IPv6双方のリスナーを確認し、確認中はソケットを保持する。
    sockets = []
    try:
        for port in PORTS:
            for family, address in ((socket.AF_INET, "0.0.0.0"), (socket.AF_INET6, "::")):
                connection = socket.socket(family)
                sockets.append(connection)
                if family == socket.AF_INET6:
                    connection.setsockopt(socket.IPPROTO_IPV6, socket.IPV6_V6ONLY, 1)
                try:
                    connection.bind((address, port))
                except OSError as error:
                    raise EnvironmentFailure(f"ポート{port}を使用できません。既存環境は変更しません。") from error
    finally:
        for connection in sockets:
            connection.close()


def prerequisites():
    for command in ("docker", "git", "java", "node", "npm", "lsof"):
        if not shutil.which(command):
            raise EnvironmentFailure(f"必要なコマンドがありません: {command}")
    version = subprocess.run(["node", "--version"], capture_output=True, text=True, timeout=10)
    if version.returncode or not version.stdout.startswith("v24."):
        raise EnvironmentFailure("検査用BFFにはNode.js 24を使用してください。")
    if not (ROOT / "frontend/node_modules/next/package.json").is_file():
        raise EnvironmentFailure("frontendの依存関係をnpm ciで準備してください。")


def stop():
    if not STATE.exists():
        say("検査環境の管理情報はありません。停止操作は行いません。")
        return
    state = json.loads(STATE.read_text())
    if identity(state["pid"]) != state["identity"]:
        raise EnvironmentFailure("監督プロセスの所有権を確認できません。資源を自動削除しません。")
    os.kill(state["pid"], signal.SIGTERM)
    for _ in range(120):
        if not STATE.exists():
            say("検査環境を停止しました。")
            return
        time.sleep(0.5)
    raise EnvironmentFailure("検査環境の停止が完了しませんでした。")


class Environment:
    def __init__(self):
        self.run_id = uuid.uuid4().hex
        self.directory = WORK / self.run_id
        self.children = []
        self.logs = []
        self.env = {**os.environ, "SECURITY_RUN_ID": self.run_id,
                    "SECURITY_DB_PASSWORD": secrets.token_urlsafe(32)}
        self.ready = False
        self.servers = []

    def write_state(self):
        state = {"pid": os.getpid(), "identity": identity(os.getpid()),
                 "run_id": self.run_id, "ready": self.ready}
        temporary = WORK / f"state-{self.run_id}.tmp"
        temporary.write_text(json.dumps(state))
        temporary.replace(STATE)

    def spawn(self, arguments, name, cwd=ROOT, environment=None):
        log = (self.directory / f"{name}.log").open("ab")
        self.logs.append(log)
        process = subprocess.Popen(arguments, cwd=cwd, env=environment or self.env,
                                   stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
        self.children.append((process, identity(process.pid)))
        return process

    def run(self, arguments, name, cwd=ROOT, environment=None, timeout=900):
        process = self.spawn(arguments, name, cwd, environment)
        try:
            code = process.wait(timeout=timeout)
        except subprocess.TimeoutExpired as error:
            raise EnvironmentFailure(f"{name}が時間上限に達しました。") from error
        if code:
            if name in ("bff-build", "api-build-migration"):
                shutil.copyfile(self.directory / f"{name}.log", WORK / f"{name}.log")
            raise EnvironmentFailure(f"{name}に失敗しました。生のログは表示しません。")

    def healthy(self, process, url, api=False):
        for _ in range(120):
            if process.poll() is not None:
                raise EnvironmentFailure("起動したプロセスが終了しました。")
            try:
                with urllib.request.urlopen(url, timeout=2) as response:
                    body = response.read()
                    if response.status == 200 and (not api or json.loads(body).get("status") == "UP"):
                        port = 8080 if api else 3000
                        listeners = subprocess.run(["lsof", "-nP", f"-iTCP:{port}", "-sTCP:LISTEN", "-t"],
                                                   capture_output=True, text=True, check=True)
                        if set(listeners.stdout.split()) != {str(process.pid)}:
                            raise EnvironmentFailure("リスナーの所有プロセスが一致しません。")
                        return
            except (OSError, ValueError):
                pass
            time.sleep(0.5)
        raise EnvironmentFailure("起動したサービスの準備が完了しませんでした。")

    def prepare(self):
        for service in ("security-postgres", "security-redis"):
            existing = subprocess.run(["docker", "ps", "-aq", "--filter",
                                       "label=com.docker.compose.project=security-check", "--filter",
                                       f"label=com.docker.compose.service={service}"],
                                      capture_output=True, text=True, check=True)
            if existing.stdout.strip():
                raise EnvironmentFailure("既存の検査用コンテナを上書きしません。所有情報を確認してください。")
        self.directory.mkdir(mode=0o700)
        self.write_state()
        self.run(COMPOSE + ["up", "-d", "--wait", "--wait-timeout", "120",
                            "security-postgres", "security-redis"], "compose", timeout=180)
        database = {**self.env, "FLYWAY_URL": "jdbc:postgresql://127.0.0.1:5432/security",
                    "FLYWAY_USER": "security", "FLYWAY_PASSWORD": self.env["SECURITY_DB_PASSWORD"],
                    "SPRING_DATASOURCE_URL": "jdbc:postgresql://127.0.0.1:5432/security",
                    "SPRING_DATASOURCE_USERNAME": "security",
                    "SPRING_DATASOURCE_PASSWORD": self.env["SECURITY_DB_PASSWORD"],
                    "OPENAPI_ENABLED": "true"}
        self.run([str(ROOT / "api/gradlew"), "-p", str(ROOT / "api"), "flywayMigrate", "bootJar",
                  "--console=plain"], "api-build-migration", environment=database)
        with (ROOT / "api/scripts/db/book_item_data.sql").open("rb") as seed:
            # 起動済みの所有コンテナだけへseedする。
            result = subprocess.run(COMPOSE + ["exec", "-T", "security-postgres", "psql",
                                    "-v", "ON_ERROR_STOP=1", "-U", "security", "-d", "security"],
                                    env=self.env, stdin=seed, stdout=subprocess.DEVNULL,
                                    stderr=subprocess.DEVNULL, timeout=60)
            if result.returncode:
                raise EnvironmentFailure("蔵書seedに失敗しました。")
        say("migration・seed完了")
        frontend = self.directory / "frontend"
        frontend.mkdir()
        files = subprocess.check_output(["git", "ls-files", "-z", "frontend"], cwd=ROOT).split(b"\0")
        for raw in files:
            if not raw:
                continue
            source = ROOT / os.fsdecode(raw)
            if source.is_symlink():
                raise EnvironmentFailure("BFFの検査用コピーにsymlinkは使用できません。")
            if not source.is_file():
                continue
            destination = frontend / source.relative_to(ROOT / "frontend")
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, destination)
        (frontend / "node_modules").symlink_to(ROOT / "frontend/node_modules", target_is_directory=True)
        bff_env = {**self.env, "SPRING_API_BASE_URL": "http://localhost:8080",
                   "BFF_PUBLIC_ORIGIN": "http://localhost:3000",
                   "REGISTRATION_REDIS_URL": "redis://127.0.0.1:6379",
                   "REGISTRATION_LIMIT_HMAC_KEY": secrets.token_urlsafe(48),
                   "NEXT_TELEMETRY_DISABLED": "1"}
        try:
            self.run(["npm", "run", "build"], "bff-build", cwd=frontend, environment=bff_env)
        except EnvironmentFailure:
            # Next.jsの初回通知による停止だけを、同じディレクトリで一度再実行する。
            if "SecurityFatalError" not in (self.directory / "bff-build.log").read_text():
                raise
            say("Next.jsが既知脆弱性の更新推奨を通知しました。指摘として残し、同じビルドを一度再実行します。")
            self.run(["npm", "run", "build"], "bff-build", cwd=frontend, environment=bff_env)
        jars = list((ROOT / "api/build/libs").glob("*.jar"))
        jars = [jar for jar in jars if not jar.name.endswith("-plain.jar")]
        if len(jars) != 1:
            raise EnvironmentFailure("起動対象のAPI JARを一意に特定できません。")
        # ビルド中に他サービスが起動した場合も既存リスナーへ接続しない。
        for port in (3000, 8080):
            with socket.socket() as connection:
                try:
                    connection.bind(("0.0.0.0", port))
                except OSError as error:
                    raise EnvironmentFailure("準備中にアプリ用ポートが使用されました。") from error
        api = self.spawn(["java", "-jar", str(jars[0])], "api", environment=database)
        self.servers.append(api)
        self.healthy(api, "http://127.0.0.1:8080/actuator/health", api=True)
        bff = self.spawn(["node", "node_modules/next/dist/bin/next", "start", "--hostname", "0.0.0.0"],
                         "bff", cwd=frontend, environment=bff_env)
        self.servers.append(bff)
        self.healthy(bff, "http://127.0.0.1:3000")
        if api.poll() is not None or bff.poll() is not None:
            raise EnvironmentFailure("アプリの起動を維持できませんでした。")
        self.ready = True
        self.write_state()
        say("API health・BFF起動完了（検査専用環境）")

    def cleanup(self):
        success = True
        def command(arguments):
            try:
                return subprocess.run(arguments, capture_output=True, text=True, timeout=20)
            except (OSError, subprocess.SubprocessError):
                return subprocess.CompletedProcess(arguments, 2, "", "")
        for process, birth in reversed(self.children):
            if process.poll() is None:
                if not birth or identity(process.pid) != birth:
                    success = False
                    continue
                try:
                    os.killpg(process.pid, signal.SIGTERM)
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    os.killpg(process.pid, signal.SIGKILL)
                    process.wait()
        # Compose全体を停止せず、実行IDで特定した資源だけを削除する。
        for kind, inspect, remove in (("container", ["inspect"], ["rm", "-f", "-v"]),
                                      ("volume", ["volume", "inspect"], ["volume", "rm"]),
                                      ("network", ["network", "inspect"], ["network", "rm"])):
            listing = ["docker", kind if kind != "container" else "ps"]
            listing += ["ls", "-q"] if kind != "container" else ["-aq"]
            result = command(listing + ["--filter", f"label=security.run={self.run_id}"])
            if result.returncode:
                success = False
                continue
            for resource in result.stdout.split():
                detail = command(["docker", *inspect, resource])
                if detail.returncode:
                    success = False
                    continue
                item = json.loads(detail.stdout)[0]
                labels = (item.get("Config", {}).get("Labels") if kind == "container" else item.get("Labels")) or {}
                if labels.get("security.run") != self.run_id:
                    success = False
                    continue
                deleted = command(["docker", *remove, resource])
                success = success and deleted.returncode == 0
        for log in self.logs:
            log.close()
        if success:
            if self.directory.exists():
                try:
                    shutil.rmtree(self.directory)
                except OSError:
                    say("検査用ファイルの削除に失敗しました。管理情報を残します。")
                    return False
            if STATE.exists() and json.loads(STATE.read_text()).get("run_id") == self.run_id:
                STATE.unlink()
            say("所有資源の片付け完了")
        else:
            say("所有資源の片付けが未完了です。管理情報を残します。")
        return success


def main():
    parser = argparse.ArgumentParser(description="検査専用環境の起動・終了")
    parser.add_argument("operation", choices=("start", "stop"))
    arguments = parser.parse_args()
    try:
        if arguments.operation == "stop":
            stop()
            return 0
        ports_available()
        prerequisites()
        WORK.mkdir(parents=True, exist_ok=True)
        with (WORK / ".lock").open("a") as lock:
            try:
                fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
            except BlockingIOError as error:
                raise EnvironmentFailure("検査環境は別の実行が使用中です。") from error
            if STATE.exists():
                raise EnvironmentFailure("既存の検査環境の管理情報が残っています。上書きしません。")
            ports_available()
            os.umask(0o077)
            environment = Environment()
            def interrupt(_signum, _frame):
                raise InterruptedError()
            signal.signal(signal.SIGTERM, interrupt)
            signal.signal(signal.SIGINT, interrupt)
            code = 0
            try:
                environment.prepare()
                while True:
                    time.sleep(0.5)
                    if any(process.poll() is not None for process in environment.servers):
                        raise EnvironmentFailure("所有プロセスが異常終了しました。")
            except InterruptedError:
                say("終了要求を受け付けました。")
            except (EnvironmentFailure, OSError, subprocess.SubprocessError) as error:
                say(str(error) if isinstance(error, EnvironmentFailure) else "検査環境の準備・維持に失敗しました。")
                code = 2
            finally:
                signal.signal(signal.SIGTERM, signal.SIG_IGN)
                signal.signal(signal.SIGINT, signal.SIG_IGN)
                if not environment.cleanup():
                    code = 2
            return code
    except (EnvironmentFailure, OSError, ValueError, KeyError) as error:
        say(str(error) if isinstance(error, EnvironmentFailure) else "検査環境の管理操作に失敗しました。")
        return 2


if __name__ == "__main__":
    sys.exit(main())
