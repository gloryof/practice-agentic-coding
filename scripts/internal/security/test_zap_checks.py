"""ZAPの完了・指摘・失敗と、片付け結果の優先順位を確認する。"""

import json
import io
from contextlib import redirect_stdout
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import Mock, patch

import zap_checks


class ZapChecksTest(unittest.TestCase):
    def test_scanner_and_cleanup_exit_contract(self):
        for scan_code, cleanup_code, expected in ((0, 0, 0), (10, 0, 1), (1, 0, 2),
                                                  (2, 0, 2), (10, 2, 2)):
            with self.subTest(scan=scan_code, cleanup=cleanup_code), tempfile.TemporaryDirectory() as temporary:
                state = Path(temporary) / "state.json"
                supervisor = Mock(pid=123)
                supervisor.poll.return_value = None
                supervisor.wait.return_value = cleanup_code
                supervisor.terminate.side_effect = lambda: state.unlink(missing_ok=True)
                scanner = Mock()
                scanner.stdout = io.StringIO("")
                scanner.wait.return_value = scan_code
                scanner.poll.return_value = scan_code
                def spawn(arguments, **_kwargs):
                    if "start" in arguments:
                        state.write_text(json.dumps({"pid": 123, "run_id": "test-run", "ready": True}))
                        return supervisor
                    return scanner
                with patch.object(zap_checks, "STATE", state), \
                     patch.object(zap_checks.signal, "signal"), \
                     patch.object(zap_checks.sys, "argv", ["runner"]), \
                     patch.object(zap_checks.subprocess, "Popen", side_effect=spawn):
                    self.assertEqual(zap_checks.main(), expected)
                    supervisor.terminate.assert_called_once()

    def test_existing_environment_is_not_adopted(self):
        with tempfile.TemporaryDirectory() as temporary:
            state = Path(temporary) / "state.json"
            state.write_text("existing")
            with patch.object(zap_checks, "STATE", state), \
                 patch.object(zap_checks.signal, "signal"), \
                 patch.object(zap_checks.sys, "argv", ["runner"]), \
                 patch.object(zap_checks.subprocess, "Popen") as spawn:
                self.assertEqual(zap_checks.main(), 2)
                spawn.assert_not_called()
                self.assertEqual(state.read_text(), "existing")

    def test_raw_errors_and_credentials_are_not_forwarded(self):
        output = io.StringIO()
        with redirect_stdout(output):
            zap_checks.stream_scan_output(io.StringIO("registry error: private-token\nZAP: 検査完了\n"))
        self.assertEqual(output.getvalue(), "ZAP: 検査完了\n")


if __name__ == "__main__":
    unittest.main()
