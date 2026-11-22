"""環境指定の誤りが開発用DBの操作につながらないことを確認する。"""

from pathlib import Path
import os
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[3]
RUNNER = ROOT / ".codex/skills/run-api-e2e/scripts/run.sh"


class ApiRunnerArgumentsTest(unittest.TestCase):
    def test_invalid_environment_never_calls_docker(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            marker = directory / "docker-called"
            docker = directory / "docker"
            docker.write_text('#!/bin/sh\ntouch "$CALL_MARKER"\nexit 99\n')
            docker.chmod(0o755)
            environment = {**os.environ, "PATH": f"{directory}:{os.environ['PATH']}",
                           "CALL_MARKER": str(marker)}
            for operation in ("start", "stop", "e2e"):
                for arguments in (("--env", "typo"),
                                  ("--env",), ("unexpected",),
                                  ("--env", "dev", "unexpected")):
                    with self.subTest(operation=operation, arguments=arguments):
                        result = subprocess.run(["bash", str(RUNNER), operation, *arguments],
                                                env=environment, capture_output=True)
                        self.assertEqual(result.returncode, 2)
                        self.assertFalse(marker.exists())

    def test_security_e2e_is_rejected(self):
        result = subprocess.run(["bash", str(RUNNER), "e2e", "--env", "security"], capture_output=True)
        self.assertEqual(result.returncode, 2)

    def test_explicit_dev_help_matches_default(self):
        def help_output(*arguments):
            return subprocess.run(["bash", str(RUNNER), "--help", *arguments],
                                  capture_output=True)
        default = help_output()
        explicit = help_output("--env", "dev")
        self.assertEqual(default.returncode, 0)
        self.assertEqual(explicit.returncode, 0)
        self.assertEqual(default.stdout, explicit.stdout)


if __name__ == "__main__":
    unittest.main()
