"""ZAP公式イメージのPython依存で実行する検査契約テスト。"""

import json
from pathlib import Path
import tempfile
import time
import unittest
from unittest.mock import patch

import zap_scan


class ZapScanTest(unittest.TestCase):
    def test_form_submission_preserves_action_fields_and_origin(self):
        form = {"action": "", "fields": [("$ACTION_REF_1", ""), ("$ACTION_1:0", "bound"),
                                           ("email", ""), ("password", "")]}
        with patch.object(zap_scan, "message", return_value=(303, "", "")) as send:
            zap_scan.submit_form("/register", form, {"email": "reader@example.com", "password": "secret"})
        method, url, body, headers = send.call_args.args
        self.assertEqual(method, "POST")
        self.assertEqual(url, zap_scan.BFF + "/register")
        self.assertIn('name="$ACTION_1:0"\r\n\r\nbound', body)
        self.assertIn("multipart/form-data; boundary=", headers["Content-Type"])
        self.assertEqual(headers["Origin"], zap_scan.BFF)

    def test_external_request_is_rejected_before_zap(self):
        with patch.object(zap_scan, "DEADLINE", time.monotonic() + 60), \
             patch.object(zap_scan, "zap") as zap:
            with self.assertRaises(zap_scan.ScanFailure):
                zap_scan.message("GET", "http://external.example/")
            zap.assert_not_called()

    def test_timeout_is_incomplete(self):
        with patch.object(zap_scan, "DEADLINE", time.monotonic() - 1):
            with self.assertRaises(zap_scan.ScanFailure):
                zap_scan.check_time()

    def test_failed_login_never_sets_authenticated_mode(self):
        with tempfile.TemporaryDirectory() as directory:
            auth = Path(directory) / "auth.json"
            with patch.object(zap_scan, "AUTH", auth), \
                 patch.object(zap_scan, "LOST", Path(directory) / "lost"), \
                 patch.object(zap_scan, "get_form", return_value={}), \
                 patch.object(zap_scan, "submit_form", return_value=(200, "", "")):
                with self.assertRaises(zap_scan.ScanFailure):
                    zap_scan.authenticate("reader@example.com", "secret")
                self.assertEqual(json.loads(auth.read_text())["mode"], "anonymous")

    def test_authenticated_bff_requires_real_search_results(self):
        with patch.object(zap_scan, "message", return_value=(200, "", "ログアウトする")), \
             patch.object(zap_scan, "verify_sender"):
            with self.assertRaises(zap_scan.ScanFailure):
                zap_scan.verify(True)

    def test_missing_form_is_incomplete(self):
        with patch.object(zap_scan, "message", return_value=(200, "", "<form></form>")):
            with self.assertRaises(zap_scan.ScanFailure):
                zap_scan.get_form("/login", "email")

    def test_normal_protected_request_rejects_failed_authentication(self):
        responses = [(200, "", "ログアウトする 世界史入門"), (401, "", "{}")]
        with patch.object(zap_scan, "message", side_effect=responses), \
             patch.object(zap_scan, "verify_sender"):
            with self.assertRaises(zap_scan.ScanFailure):
                zap_scan.verify(True)

    def test_anonymous_protected_request_requires_unauthorized_response(self):
        responses = [(200, "", "ログインが必要です"), (401, "", "{}")]
        with patch.object(zap_scan, "message", side_effect=responses), \
             patch.object(zap_scan, "verify_sender"):
            zap_scan.verify(False)

    def test_external_schema_refs_are_rejected_recursively(self):
        for reference in ("https://external.example/schema", "other.json", "file:///private/schema"):
            with self.subTest(reference=reference):
                with self.assertRaises(zap_scan.ScanFailure):
                    zap_scan.sanitize_openapi({"components": {"schemas": {"item": {"$ref": reference}}}})

    def test_nested_server_overrides_are_removed(self):
        specification = {"servers": [{"url": "https://external.example"}],
                         "paths": {"/api/v1/example": {"post": {"servers": [{"url": "https://other.example"}],
                                                                 "schema": {"$ref": "#/components/schemas/item"}}}}}
        zap_scan.sanitize_openapi(specification)
        self.assertNotIn("servers", specification)
        self.assertNotIn("servers", specification["paths"]["/api/v1/example"]["post"])

    def test_stopped_and_empty_active_scans_are_incomplete(self):
        for state, requests in (("STOPPED", "50"), ("FINISHED", "0")):
            with self.subTest(state=state, requests=requests):
                responses = [{"context": {"id": "1"}}, {"scan": "2"},
                             {"scans": [{"id": "2", "state": state, "reqCount": requests}]}]
                with patch.object(zap_scan, "zap", side_effect=responses), \
                     patch.object(zap_scan, "wait_scan"):
                    with self.assertRaises(zap_scan.ScanFailure):
                        zap_scan.active("BFF")


if __name__ == "__main__":
    unittest.main()
