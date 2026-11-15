"""検査対象との照合、解析失敗、結果の秘匿と実ルールの検出を確認する。"""

from contextlib import redirect_stdout
import io
import json
import os
from pathlib import Path
import subprocess
import tempfile
import tarfile
import unittest
from unittest.mock import patch

import semgrep_checks as checks
from trivy_checks import COMPOSE


def report(*, findings=None, errors=None, scanned=None):
    return {"version": checks.VERSION, "results": findings or [], "errors": errors or [],
            "paths": {"scanned": scanned or []}, "engine_requested": "OSS"}


class SemgrepChecksTest(unittest.TestCase):
    def test_clean_findings_and_errors_have_distinct_statuses(self):
        finding = {"path": "safe.ts", "start": {"line": 1}, "check_id": "test-rule",
                   "extra": {"severity": "WARNING", "lines": "DO-NOT-DISPLAY"}}
        cases = [(report(scanned=["safe.ts"]), 0),
                 (report(findings=[finding], scanned=["safe.ts"]), 1),
                 (report(errors=[{"type": "Syntax error", "message": "DO-NOT-DISPLAY"}], scanned=["safe.ts"]), 2),
                 (report(scanned=[]), 2),
                 (report(findings=[finding], scanned=[]), 2)]
        for data, expected in cases:
            with self.subTest(expected=expected), redirect_stdout(io.StringIO()) as output:
                self.assertEqual(checks.summarize(data, {"safe.ts"}), expected)
                self.assertNotIn("DO-NOT-DISPLAY", output.getvalue())

    def test_malformed_and_wrong_version_reports_cannot_be_clean(self):
        for data in [{}, [], {**report(), "version": "wrong"}, {**report(), "errors": False}]:
            with self.subTest(data=data), self.assertRaises((checks.Incomplete, KeyError, TypeError)):
                checks.summarize(data, {"example.kt"})

    def make_archive(self, paths):
        data = io.BytesIO()
        with tarfile.open(fileobj=data, mode="w:gz") as bundle:
            for path in paths:
                content = b"rules: []\n"
                item = tarfile.TarInfo("semgrep-rules-" + "a"*40 + "/" + path)
                item.size = len(content)
                bundle.addfile(item, io.BytesIO(content))
        return data.getvalue()

    def test_new_security_rules_are_included_without_an_allowlist(self):
        paths = ["kotlin/lang/security/new-rule.yaml", "javascript/new-framework/security/new-rule.yaml",
                 "typescript/react/security/new-rule.yaml", "python/lang/security/excluded.yaml"]
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            with patch.object(checks, "download", side_effect=[json.dumps({"sha": "a"*40}).encode(), self.make_archive(paths)]), \
                    redirect_stdout(io.StringIO()):
                checks.prepare_rules(directory)
            self.assertEqual(len(list((directory / "vendor").rglob("*.yaml"))), 3)
            self.assertTrue((directory / "vendor/javascript/new-framework/security/new-rule.yaml").exists())
            self.assertTrue((directory / "local/parse-check.yaml").exists())

    def test_unsafe_archive_paths_are_rejected(self):
        with tempfile.TemporaryDirectory() as temporary, \
                patch.object(checks, "download", side_effect=[json.dumps({"sha": "a"*40}).encode(),
                                                               self.make_archive(["../escape.yaml"])]):
            with self.assertRaises(checks.Incomplete):
                checks.prepare_rules(Path(temporary))

    def test_target_scope_and_working_tree_content(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            root, output = directory / "repo", directory / "input"
            root.mkdir()
            output.mkdir()
            subprocess.run(["git", "init", "-q", str(root)], check=True)
            sources = ["api/src/test/Test.kt", "frontend/app/page.tsx", "other.ts", "frontend/package.json"]
            for name in sources:
                file = root / name
                file.parent.mkdir(parents=True, exist_ok=True)
                file.write_text("initial")
            subprocess.run(["git", "-C", str(root), "add", *sources], check=True)
            (root / "frontend/app/page.tsx").write_text("current")
            (root / "frontend/untracked.ts").write_text("do not copy")
            with patch.object(checks, "ROOT", root):
                self.assertEqual(checks.stage_sources(output), {"api/src/test/Test.kt", "frontend/app/page.tsx"})
            self.assertEqual((output / "frontend/app/page.tsx").read_text(), "current")
            self.assertFalse((output / "frontend/untracked.ts").exists())
            self.assertFalse((output / "frontend/package.json").exists())
            (root / "frontend/app/page.tsx").unlink()
            (root / "frontend/app/page.tsx").symlink_to(root / "other.ts")
            with patch.object(checks, "ROOT", root), self.assertRaises(checks.Incomplete):
                checks.stage_sources(output)

    def test_download_failure_prevents_scan(self):
        with patch.object(checks.sys, "argv", ["semgrep_checks.py"]), \
                patch.object(checks, "prepare_rules", side_effect=checks.Incomplete("取得失敗")), \
                patch.object(checks, "scan") as scan, redirect_stdout(io.StringIO()):
            self.assertEqual(checks.main(), 2)
            scan.assert_not_called()

    def test_skipped_rules_and_taint_timeouts_are_incomplete(self):
        for addition in [{"skipped_rules": ["unsupported"]}, {"time": {"fixpoint_timeouts": ["example.kt"]}}]:
            with redirect_stdout(io.StringIO()):
                self.assertEqual(checks.summarize({**report(scanned=["example.kt"]), **addition}, {"example.kt"}), 2)


@unittest.skipUnless(os.environ.get("RUN_SEMGREP_INTEGRATION") == "1", "実ツール検証は明示実行")
class SemgrepIntegrationTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temporary_rules = tempfile.TemporaryDirectory(prefix="semgrep-rules-test-")
        cls.addClassCleanup(cls.temporary_rules.cleanup)
        cls.rules = Path(cls.temporary_rules.name)
        checks.prepare_rules(cls.rules)

    def test_compose_is_offline_and_mounts_only_readonly_inputs(self):
        config = checks.parse_json(checks.run(COMPOSE + ["config", "--format", "json"]))
        service = config["services"]["semgrep"]
        self.assertEqual(service["network_mode"], "none")
        self.assertTrue(service["read_only"])
        self.assertEqual(service["user"], "65532:65532")
        self.assertEqual(service["cap_drop"], ["ALL"])
        self.assertEqual({mount["target"] for mount in service["volumes"]}, {"/input", "/rules"})
        self.assertTrue(all(mount["read_only"] for mount in service["volumes"]))
        self.assertNotIn("SEMGREP_APP_TOKEN", service["environment"])
        self.assertEqual(service["environment"]["SEMGREP_SEND_METRICS"], "off")
        self.assertRegex(service["image"], r"^semgrep/semgrep:1\.185\.0@sha256:[0-9a-f]{64}$")

    def scan_files(self, files):
        with tempfile.TemporaryDirectory(prefix="semgrep-fixture-") as temporary:
            directory = Path(temporary)
            for name, text in files.items():
                file = directory / name
                file.parent.mkdir(parents=True, exist_ok=True)
                file.write_text(text)
            return checks.scan(directory, self.rules)

    def test_categories_and_nosemgrep_cannot_silence_findings(self):
        data = self.scan_files({
            "test/Unsafe.kt": '''
fun audit(input: String, db: Any) {
    java.security.MessageDigest.getInstance("MD5") // nosemgrep
    Runtime.getRuntime().exec("command " + input)
    db.executeQuery("SELECT * FROM books WHERE id=" + input)
    java.io.File(input)
    javax.net.ssl.SSLContext.getInstance("TLSv1")
}
''',
            "unsafe.ts": '''
import * as cp from "child_process";
import * as fs from "fs";
function audit(command: string, path: string, url: string) {
    cp.exec(command);
    fs.readFileSync(path);
    return fetch(url);
}
''',
            "Unsafe.tsx": '''
function Example(html: string) {
    return <div dangerouslySetInnerHTML={{__html: html}} />;
}
''',
        })
        self.assertEqual(data["errors"], [])
        ids = {item["check_id"].rsplit(".", 1)[-1] for item in data["results"]}
        self.assertTrue({"use-of-md5", "command-injection-formatted-runtime-call", "local-kotlin-concatenated-sql",
                         "local-kotlin-dynamic-file", "local-kotlin-weak-tls", "detect-child-process",
                         "detect-non-literal-fs-filename", "local-typescript-fetch-url",
                         "react-dangerouslysetinnerhtml"}.issubset(ids), ids)
        with redirect_stdout(io.StringIO()):
            self.assertEqual(checks.summarize(data, {"test/Unsafe.kt", "unsafe.ts", "Unsafe.tsx"}), 1)

    def test_safe_code_is_clean(self):
        data = self.scan_files({"Safe.kt": '''
fun safe() {
    java.security.MessageDigest.getInstance("SHA-256")
    java.io.File("fixed.txt")
    javax.net.ssl.SSLContext.getInstance("TLSv1.3")
}
''', "safe.tsx": 'function Safe() { return <div>固定の本文</div>; }'})
        with redirect_stdout(io.StringIO()):
            self.assertEqual(checks.summarize(data, {"Safe.kt", "safe.tsx"}), 0)

    def test_invalid_kotlin_is_incomplete(self):
        data = self.scan_files({"broken.kt": 'fun broken() {\n val = ;\n }'})
        with redirect_stdout(io.StringIO()):
            self.assertEqual(checks.summarize(data, {"broken.kt"}), 2, json.dumps(data))

    def test_oversize_file_is_not_silently_clean(self):
        data = self.scan_files({"large.ts": "//" + "x" * 1000000})
        with redirect_stdout(io.StringIO()):
            self.assertEqual(checks.summarize(data, {"large.ts"}), 2)


if __name__ == "__main__":
    unittest.main()
