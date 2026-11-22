"""起動前停止と、片付け対象の所有権を外部サービスなしで検証する。"""

import json
from pathlib import Path
import socket
import subprocess
import tempfile
import unittest
from unittest.mock import Mock, patch

import security_environment as environment


class DynamicEnvironmentTest(unittest.TestCase):
    def test_busy_port_is_rejected_without_touching_listener(self):
        with socket.socket() as listener:
            listener.bind(("127.0.0.1", 0))
            listener.listen()
            port = listener.getsockname()[1]
            with patch.object(environment, "PORTS", (port,)):
                with self.assertRaises(environment.EnvironmentFailure):
                    environment.ports_available()
            self.assertEqual(listener.getsockname()[1], port)

    def test_port_failure_happens_before_creating_work_or_calling_docker(self):
        with tempfile.TemporaryDirectory() as temporary:
            work = Path(temporary) / "absent"
            with patch.object(environment, "WORK", work), \
                 patch.object(environment.sys, "argv", ["runner", "start"]), \
                 patch.object(environment, "ports_available", side_effect=environment.EnvironmentFailure("busy")), \
                 patch.object(environment.subprocess, "run") as command:
                self.assertEqual(environment.main(), 2)
                self.assertFalse(work.exists())
                command.assert_not_called()

    def test_stop_rejects_reused_pid(self):
        with tempfile.TemporaryDirectory() as temporary:
            state = Path(temporary) / "environment.json"
            state.write_text(json.dumps({"pid": 123, "identity": "original"}))
            with patch.object(environment, "STATE", state), \
                 patch.object(environment, "identity", return_value="replacement"), \
                 patch.object(environment.os, "kill") as kill:
                with self.assertRaises(environment.EnvironmentFailure):
                    environment.stop()
                kill.assert_not_called()

    def test_cleanup_deletes_only_resources_with_matching_label(self):
        instance = environment.Environment()
        calls = []
        def command(arguments, **_kwargs):
            calls.append(arguments)
            if "--filter" in arguments:
                return subprocess.CompletedProcess(arguments, 0, "owned\nforeign\n", "")
            if "inspect" in arguments:
                label = instance.run_id if arguments[-1] == "owned" else "another-run"
                data = [{"Config": {"Labels": {"security.run": label}}, "Labels": {"security.run": label}}]
                return subprocess.CompletedProcess(arguments, 0, json.dumps(data), "")
            return subprocess.CompletedProcess(arguments, 0, "", "")
        with patch.object(environment.subprocess, "run", side_effect=command):
            self.assertFalse(instance.cleanup())
        deletions = [call for call in calls if "rm" in call]
        self.assertEqual(len(deletions), 3)
        self.assertTrue(all(call[-1] == "owned" for call in deletions))
        self.assertFalse(any("down" in call for call in calls))

    def test_failed_deletion_retains_state(self):
        instance = environment.Environment()
        with tempfile.TemporaryDirectory() as temporary:
            state = Path(temporary) / "state.json"
            state.write_text(json.dumps({"run_id": instance.run_id}))
            with patch.object(environment, "STATE", state), \
                 patch.object(environment.subprocess, "run", return_value=subprocess.CompletedProcess([], 1, "", "")):
                self.assertFalse(instance.cleanup())
                self.assertTrue(state.exists())

    def test_cleanup_does_not_signal_reused_process(self):
        instance = environment.Environment()
        process = Mock(pid=123)
        process.poll.return_value = None
        instance.children = [(process, "owned-birth")]
        with tempfile.TemporaryDirectory() as temporary:
            with patch.object(environment, "STATE", Path(temporary) / "absent"), \
                 patch.object(environment, "identity", return_value="different-birth"), \
                 patch.object(environment.os, "killpg") as kill, \
                 patch.object(environment.subprocess, "run", return_value=subprocess.CompletedProcess([], 0, "", "")):
                self.assertFalse(instance.cleanup())
                kill.assert_not_called()

    def test_prepare_failure_and_interrupt_both_cleanup(self):
        for failure, expected in ((environment.EnvironmentFailure("failed"), 2), (InterruptedError(), 0)):
            with self.subTest(failure=type(failure).__name__), tempfile.TemporaryDirectory() as temporary:
                instance = Mock()
                instance.prepare.side_effect = failure
                instance.cleanup.return_value = True
                with patch.object(environment, "WORK", Path(temporary)), \
                     patch.object(environment, "STATE", Path(temporary) / "state.json"), \
                     patch.object(environment, "ports_available"), \
                     patch.object(environment, "prerequisites"), \
                     patch.object(environment.signal, "signal"), \
                     patch.object(environment, "Environment", return_value=instance), \
                     patch.object(environment.sys, "argv", ["runner", "start"]):
                    self.assertEqual(environment.main(), expected)
                    instance.cleanup.assert_called_once()


if __name__ == "__main__":
    unittest.main()
