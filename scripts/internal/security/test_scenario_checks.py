"""シナリオ失敗時の続行、時間上限と出力抑止を確認する。"""

import subprocess
import io
import tempfile
from pathlib import Path
from contextlib import redirect_stdout
import unittest
from unittest.mock import Mock, patch

import scenario_checks


class ScenarioChecksTest(unittest.TestCase):
    def test_failure_continues_without_forwarding_raw_output(self):
        failed = Mock(pid=123)
        failed.communicate.return_value = (None, None)
        failed.returncode = 1
        failed.poll.return_value = 1
        passed = Mock(pid=124)
        passed.communicate.return_value = (b'{"suites": [], "stats": {"expected": 10}}', None)
        passed.returncode = 0
        passed.poll.return_value = 0
        with patch.object(scenario_checks.subprocess, "Popen", side_effect=[failed, passed]) as spawn:
            self.assertEqual(scenario_checks.run_scenarios(float("inf")), 2)
            self.assertEqual(spawn.call_count, 2)
            for call in spawn.call_args_list:
                self.assertEqual(call.kwargs["stderr"], subprocess.DEVNULL)
                self.assertTrue(call.kwargs["start_new_session"])

    def test_timeout_terminates_owned_process_group(self):
        process = Mock(pid=123)
        process.communicate.side_effect = subprocess.TimeoutExpired("test", 1)
        process.wait.return_value = 0
        process.poll.return_value = None
        with patch.object(scenario_checks.subprocess, "Popen", return_value=process), \
             patch.object(scenario_checks.os, "killpg") as kill:
            with self.assertRaises(subprocess.TimeoutExpired):
                scenario_checks.run_scenarios(float("inf"))
            kill.assert_called_once_with(123, scenario_checks.signal.SIGTERM)

    def test_empty_or_skipped_browser_run_is_incomplete(self):
        self.assertEqual(scenario_checks.browser_report_result(b'{}'), 2)
        self.assertEqual(scenario_checks.browser_report_result(
            b'{"stats": {"expected": 10, "skipped": 1}}'), 2)

    def test_log_candidates_do_not_expose_values_and_missing_logs_are_incomplete(self):
        with tempfile.TemporaryDirectory() as temporary:
            work = Path(temporary)
            (work / "owned").mkdir()
            with patch.object(scenario_checks, "WORK", work):
                self.assertEqual(scenario_checks.check_runtime_logs("owned"), 2)
                (work / "owned/api.log").write_text("safe metadata")
                (work / "owned/bff.log").write_text("safe metadata")
                self.assertEqual(scenario_checks.check_runtime_logs("owned"), 0)
                secret = "synthetic-reader@example.com"
                (work / "owned/api.log").write_text(secret)
                output = io.StringIO()
                with redirect_stdout(output):
                    self.assertEqual(scenario_checks.check_runtime_logs("owned"), 1)
                self.assertNotIn(secret, output.getvalue())


if __name__ == "__main__":
    unittest.main()
