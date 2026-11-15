"""公開ルールを毎回取得し、ソース検査はネットワークなしで実行する。"""

import hashlib
import io
import os
from pathlib import Path
import re
import shutil
import stat
import sys
import tarfile
import tempfile
import urllib.error
import urllib.request

from trivy_checks import ROOT, Incomplete, compose_run, parse_json, run, safe


RULES = Path(__file__).resolve().parent / "semgrep"
VERSION = "1.185.0"
EXTENSIONS = {".kt", ".kts", ".ts", ".tsx", ".js", ".jsx", ".mjs", ".cjs", ".mts", ".cts"}


def download(url, limit):
    # 公開URLのみを使用し、アカウント・トークン・ソースは送信しない。
    request = urllib.request.Request(url, headers={"User-Agent": "library-security-check"})
    try:
        with urllib.request.urlopen(request, timeout=60) as response:
            data = response.read(limit + 1)
    except (urllib.error.URLError, TimeoutError, OSError) as error:
        raise Incomplete("公開ルールのオンライン取得に失敗（認証不要、接続・取得制限を確認）") from error
    if len(data) > limit:
        raise Incomplete("公開ルールの取得サイズが上限を超過")
    return data


def prepare_rules(destination):
    metadata = parse_json(download("https://api.github.com/repos/semgrep/semgrep-rules/commits/develop", 2_000_000))
    commit = metadata["sha"]
    if not isinstance(commit, str) or not re.fullmatch(r"[0-9a-f]{40}", commit):
        raise Incomplete("公開ルールのコミットが不正")
    archive = download("https://codeload.github.com/semgrep/semgrep-rules/tar.gz/" + commit, 30_000_000)
    count = 0
    languages = set()
    total = 0
    prefix = "semgrep-rules-" + commit
    with tarfile.open(fileobj=io.BytesIO(archive), mode="r:gz") as bundle:
        for index, member in enumerate(bundle):
            if index > 30000 or member.offset_data + member.size > 100_000_000:
                raise Incomplete("公開ルールのアーカイブ展開上限を超過")
            path = Path(member.name)
            parts = path.parts
            if path.is_absolute() or ".." in parts or not parts or parts[0] != prefix:
                raise Incomplete("公開ルールのアーカイブに不正なパス")
            if len(parts) < 3 or parts[1] not in {"kotlin", "javascript", "typescript"} or "security" not in parts:
                continue
            if path.suffix not in {".yaml", ".yml"} or path.name.endswith((".test.yaml", ".test.yml")):
                continue
            if not member.isfile() or member.size > 1_000_000:
                raise Incomplete("公開ルールのファイル種別またはサイズが不正")
            total += member.size
            if total > 20_000_000 or count >= 2000:
                raise Incomplete("公開ルールの展開サイズまたは件数が上限を超過")
            output = destination / "vendor" / Path(*parts[1:])
            output.parent.mkdir(parents=True, exist_ok=True)
            with bundle.extractfile(member) as source:
                output.write_bytes(source.read())
            count += 1
            languages.add(parts[1])
    if languages != {"kotlin", "javascript", "typescript"}:
        raise Incomplete("必要な言語の公開ルールが空")
    shutil.copytree(RULES / "local", destination / "local")
    print(f"公開ルール: {count}ファイル、取得コミット: {commit}、アーカイブSHA-256: {hashlib.sha256(archive).hexdigest()}", flush=True)


def stage_sources(destination):
    entries = run(["git", "-C", str(ROOT), "ls-files", "-z", "--cached", "--", "api/src", "frontend"])
    expected = set()
    for raw in entries.split(b"\0"):
        if not raw:
            continue
        relative = Path(os.fsdecode(raw))
        if relative.suffix not in EXTENSIONS:
            continue
        if relative.is_absolute() or ".." in relative.parts:
            raise Incomplete("ソースのパスが不正")
        source = ROOT / relative
        if any(path.is_symlink() for path in [source, *list(source.parents)[:len(relative.parts) - 1]]):
            raise Incomplete(f"symlinkは対象外: {safe(relative)}")
        try:
            mode = source.lstat().st_mode
        except FileNotFoundError:
            continue
        if not stat.S_ISREG(mode):
            raise Incomplete(f"通常ファイル以外は対象外: {safe(relative)}")
        output = destination / relative
        output.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, output)
        expected.add(relative.as_posix())
    if not expected:
        raise Incomplete("対象ソースが空")
    return expected


def scan(directory, rules):
    # 空ファイルで組み込みのtest等の除外を置き換え、対象一覧との照合を可能にする。
    (directory / ".semgrepignore").write_text("")
    command = ["scan", "--oss-only", "--config", "/rules/vendor", "--config", "/rules/local",
               "--json", "--verbose", "--metrics", "off", "--disable-version-check",
               "--disable-nosem", "--no-git-ignore", "--jobs", "2", "--timeout", "30",
               "--timeout-threshold", "1", "--max-memory", "1024", "--max-target-bytes", "1000000", "."]
    for extension in sorted(EXTENSIONS):
        command += ["--include", "*" + extension]
    raw = compose_run("semgrep", command,
                      env={**os.environ, "SEMGREP_INPUT": str(directory), "SEMGREP_RULES": str(rules)}, timeout=600)
    return parse_json(raw)


def normalize(path):
    return Path(path.removeprefix("/input/")).as_posix()


def summarize(report, expected):
    if not isinstance(report, dict) or report.get("version") != VERSION or report.get("engine_requested") != "OSS":
        raise Incomplete("Semgrepの版または結果形式が不一致")
    findings, errors = report["results"], report["errors"]
    scanned = report["paths"]["scanned"]
    if not all(isinstance(items, list) for items in [findings, errors, scanned]):
        raise Incomplete("検査結果の一覧が不正")
    status = int(bool(findings))
    for finding in findings:
        print("ソース指摘:", safe(finding["path"]), "行", safe(finding["start"]["line"]),
              safe(finding["check_id"]), safe(finding["extra"]["severity"]))
    for error in errors:
        print("解析エラー:", safe(error.get("type", "不明")), safe(error.get("path", "ルールまたは全体")))
    missing = expected - {normalize(path) for path in scanned}
    for path in sorted(missing):
        print("未検査:", safe(path))
    if errors or missing:
        status = 2
    if report.get("skipped_rules") or report.get("time", {}).get("fixpoint_timeouts"):
        print("ルールの省略またはデータフロー解析の時間上限により未完了")
        status = 2
    print(f"ソース検査: 対象{len(expected)}件、走査{len(scanned)}件、指摘{len(findings)}件、解析エラー{len(errors)}件")
    print("制限: CEの単一関数解析と選定ルールの対象構文に限定。未追跡ファイル・依存ライブラリ・関数間の認可は別途確認")
    return status


def main():
    if len(sys.argv) != 1:
        print("使用方法: check-source.sh")
        return 2
    try:
        with tempfile.TemporaryDirectory(prefix="semgrep-input-") as temporary:
            directory = Path(temporary) / "source"
            directory.mkdir()
            rules = Path(temporary) / "rules"
            rules.mkdir()
            prepare_rules(rules)
            expected = stage_sources(directory)
            return summarize(scan(directory, rules), expected)
    except Incomplete as error:
        print(f"source: 失敗・未完了（{error}）")
    except (OSError, KeyError, TypeError, ValueError, AttributeError, IndexError, tarfile.TarError):
        print("source: 失敗・未完了（準備または結果形式を確認してください）")
    except KeyboardInterrupt:
        print("source: 中断・未完了")
    return 2


if __name__ == "__main__":
    sys.exit(main())
