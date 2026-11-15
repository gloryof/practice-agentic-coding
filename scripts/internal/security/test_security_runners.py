"""専用の一時ディレクトリで、Dockerやアプリを起動せず入口の契約を検証する。"""

import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[3]


class SecurityRunnersTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="security-runner-test-")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.checks = self.root / "scripts/internal/security"
        self.checks.mkdir(parents=True)
        self.runner = self.root / "scripts/run-security-checks.sh"
        shutil.copy(ROOT / "scripts/run-security-checks.sh", self.runner)

    def run_group(self, *arguments):
        return subprocess.run(["bash", str(self.runner), *arguments], capture_output=True, text=True)

    def install_checks(self, codes):
        for name, code in zip(
            ["npm-audit", "java-dependencies", "container-images", "source", "secrets"], codes
        ):
            (self.checks / f"check-{name}.sh").write_text(f"exit {code}\n")

    def test_aggregation_and_continuation(self):
        for codes, expected in [([0]*5, 0), ([1, 0, 0, 0, 0], 1),
                                ([1, 2, 0, 0, 0], 2), ([99, 0, 0, 0, 0], 2)]:
            with self.subTest(codes=codes):
                self.install_checks(codes)
                result = self.run_group("--group", "static")
                self.assertEqual(result.returncode, expected)
                self.assertIn("secrets:", result.stdout)

    def test_unimplemented_checks_are_incomplete(self):
        self.install_checks([0])
        result = self.run_group("--group", "static")
        self.assertEqual(result.returncode, 2)
        self.assertIn("source: 未実装", result.stdout)
        self.assertEqual(self.run_group("--group", "dynamic").returncode, 2)

    def test_invalid_arguments(self):
        for args in [[], ["--all"], ["--only", "npm-audit"],
                     ["--group", "unknown"], ["--group", "static", "extra"]]:
            self.assertEqual(self.run_group(*args).returncode, 2)

    def test_compose_errors_are_not_findings(self):
        script = self.checks / "check-npm-audit.sh"
        shutil.copy(ROOT / "scripts/internal/security/check-npm-audit.sh", script)
        binary = self.root / "bin"
        binary.mkdir()
        docker = binary / "docker"
        docker.write_text('#!/bin/sh\nprintf "%s\\n" "$@" > "$CALL_LOG"\nexit "$DOCKER_STATUS"\n')
        docker.chmod(0o755)
        log = self.root / "calls"
        for status, expected in [(0, 0), (10, 1), (1, 2), (2, 2), (125, 2), (130, 2)]:
            env = {**os.environ, "PATH": f"{binary}:{os.environ['PATH']}",
                   "DOCKER_STATUS": str(status), "CALL_LOG": str(log)}
            result = subprocess.run(["bash", str(script)], env=env, capture_output=True)
            self.assertEqual(result.returncode, expected)
            args = log.read_text().splitlines()
            self.assertEqual(args[-5:], ["run", "--rm", "--no-deps", "-T", "npm-audit"])
            self.assertIn("security-check", args)


if __name__ == "__main__":
    unittest.main()
