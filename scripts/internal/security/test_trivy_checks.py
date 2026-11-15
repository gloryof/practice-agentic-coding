"""出力の秘匿、未検査の判定、入力境界と実ツールfixtureを検証する。"""

from contextlib import redirect_stdout
import io
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile

import trivy_checks as checks


class TrivyChecksTest(unittest.TestCase):
    def test_secret_value_and_source_are_never_displayed(self):
        result = [{"Target": "example.txt", "Secrets": [{"RuleID": "test-rule", "StartLine": 3,
                   "Match": "DO-NOT-DISPLAY", "Code": {"Lines": ["DO-NOT-DISPLAY"]}}]}]
        output = io.StringIO()
        with redirect_stdout(output):
            self.assertEqual(checks.summarize(result, secrets=True), 1)
        self.assertNotIn("DO-NOT-DISPLAY", output.getvalue())
        self.assertIn("test-rule", output.getvalue())
        self.assertIn("example.txt", output.getvalue())

    def test_unknown_jar_is_incomplete_even_when_no_vulnerability_is_reported(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            (directory / "unknown.jar").write_bytes(b"unknown")
            (directory / "known.jar").write_bytes(b"known")
            results = [{"Target": "Java", "Type": "jar", "Packages": [
                {"Name": "known", "FilePath": "known.jar"}]}]
            with redirect_stdout(io.StringIO()):
                self.assertEqual(checks.check_java_coverage(directory, results), 2)
                self.assertEqual(checks.check_java_coverage(directory, []), 2)
            (directory / "unknown.jar").unlink()
            self.assertEqual(checks.check_java_coverage(directory, results), 0)

    def test_only_current_tracked_regular_files_are_staged(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            subprocess.run(["git", "init", "-q", str(root)], check=True)
            (root / "tracked").write_text("index version")
            (root / "deleted").write_text("deleted")
            subprocess.run(["git", "-C", str(root), "add", "tracked", "deleted"], check=True)
            (root / "tracked").write_text("working tree version")
            (root / "deleted").unlink()
            (root / "untracked-secret").write_text("must not copy")
            destination = root / "snapshot"
            destination.mkdir()
            with patch.object(checks, "ROOT", root):
                checks.stage_tracked_files(destination)
            self.assertEqual((destination / "tracked").read_text(), "working tree version")
            self.assertEqual([path.name for path in destination.iterdir()], ["tracked"])
            (root / "tracked").unlink()
            (root / "tracked").symlink_to(root / "untracked-secret")
            with patch.object(checks, "ROOT", root), self.assertRaises(checks.Incomplete):
                checks.stage_tracked_files(destination)

    def test_failed_verification_prevents_all_scans(self):
        with patch.object(checks.sys, "argv", ["trivy_checks.py", "secrets"]), \
                patch.object(checks, "compose_config", return_value={}), \
                patch.object(checks, "verify", side_effect=checks.Incomplete("署名検証失敗")), \
                patch.object(checks, "scan") as scan, redirect_stdout(io.StringIO()):
            self.assertEqual(checks.main(), 2)
            scan.assert_not_called()

    def test_wrong_source_or_mutable_tag_is_rejected_before_execution(self):
        for image in ["ghcr.io/aquasecurity/trivy:latest", "example.org/trivy:0.76.2@sha256:" + "a"*64]:
            with patch.object(checks, "compose_run") as execute, self.assertRaises(checks.Incomplete):
                checks.verify({"services": {"trivy": {"image": image}}})
            execute.assert_not_called()

    def test_nonzero_tool_exit_and_invalid_json_are_incomplete(self):
        result = subprocess.CompletedProcess(["docker"], 1, b"", b"unauthorized DO-NOT-DISPLAY")
        with patch.object(checks.subprocess, "run", return_value=result):
            with self.assertRaises(checks.Incomplete) as raised:
                checks.run(["docker"])
            self.assertIn("認証", str(raised.exception))
            self.assertNotIn("DO-NOT-DISPLAY", str(raised.exception))
        for raw in [b"broken", b"\xff"]:
            with self.assertRaises(checks.Incomplete):
                checks.parse_json(raw)

    def test_timeout_removes_only_owned_container(self):
        with patch.object(checks, "run", side_effect=checks.Incomplete("時間上限")), \
                patch.object(checks.subprocess, "run") as cleanup:
            with self.assertRaises(checks.Incomplete):
                checks.compose_run("trivy", ["fs", "/input"])
            arguments = cleanup.call_args.args[0]
            self.assertEqual(arguments[:3], ["docker", "rm", "--force"])
            self.assertTrue(arguments[3].startswith("security-trivy-"))

    def test_malformed_results_are_not_a_clean_scan(self):
        for invalid in [{}, False, "", [{"Target": "x", "Secrets": {}}], [None]]:
            with self.subTest(invalid=invalid), \
                    patch.object(checks, "compose_run", return_value=b"ignored"), \
                    patch.object(checks, "parse_json", return_value={"SchemaVersion": 2, "Results": invalid}):
                with self.assertRaises(checks.Incomplete):
                    checks.scan(Path("/tmp"), ["fs", "/input"], "secret")

    def test_container_failure_does_not_skip_redis(self):
        config = {"services": {"postgres": {"image": "postgres:16"},
                               "registration-redis": {"image": "redis:7-alpine"}}}
        with patch.object(checks, "run", return_value=b"{}"), \
                patch.object(checks, "parse_json", return_value=config), \
                patch.object(checks, "scan", side_effect=checks.Incomplete("接続エラー")) as scan, \
                redirect_stdout(io.StringIO()):
            self.assertEqual(checks.container_images(Path("/tmp")), 2)
            self.assertEqual(scan.call_count, 2)


@unittest.skipUnless(os.environ.get("RUN_TRIVY_INTEGRATION") == "1", "Dockerを使う実ツール検証は明示実行")
class TrivyIntegrationTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        checks.verify(checks.compose_config())

    def test_synthetic_secret_is_detected_without_exposing_value(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            # 実在しないテスト用の鍵を実行時に組み立てる。
            secret = "AKIA" + "ABCDEFGHIJKLMNOP"
            (directory / "fixture.txt").write_text("aws_access_key_id = " + secret)
            _, results = checks.scan(directory, ["fs", "/input"], "secret")
            output = io.StringIO()
            with redirect_stdout(output):
                self.assertEqual(checks.summarize(results, secrets=True), 1)
            self.assertNotIn(secret, output.getvalue())

    def test_vulnerable_jar_and_unidentified_jar(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            # コードを含まないJARメタデータで既知脆弱性の検出を確認する。
            with zipfile.ZipFile(directory / "log4j-core-2.14.1.jar", "w") as jar:
                jar.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n")
                jar.writestr("META-INF/maven/org.apache.logging.log4j/log4j-core/pom.properties",
                             "groupId=org.apache.logging.log4j\nartifactId=log4j-core\nversion=2.14.1\n")
            (directory / "unknown.jar").write_bytes(b"unidentified")
            _, results = checks.scan(directory, ["rootfs", "/input"], "vuln")
            ids = {v["VulnerabilityID"] for r in results for v in r.get("Vulnerabilities") or []}
            self.assertIn("CVE-2021-44228", ids)
            with redirect_stdout(io.StringIO()):
                self.assertEqual(checks.check_java_coverage(directory, results), 2)
            (directory / "unknown.jar").unlink()
            self.assertEqual(checks.check_java_coverage(directory, results), 0)


if __name__ == "__main__":
    unittest.main()
