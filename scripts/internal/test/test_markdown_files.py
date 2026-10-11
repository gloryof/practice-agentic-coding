#!/usr/bin/env python3
"""Integration tests for local and CI Markdown selection."""

import importlib.util
from pathlib import Path
import subprocess
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parent.parent / "paths/markdown_files.py"
spec = importlib.util.spec_from_file_location("markdown_files", SCRIPT)
markdown_files = importlib.util.module_from_spec(spec)
spec.loader.exec_module(markdown_files)


class MarkdownFilesTest(unittest.TestCase):
    def git(self, *args):
        return subprocess.check_output(["git", *args], cwd=self.root)

    def write(self, name, value="本文。\n"):
        path = self.root / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(value, encoding="utf-8")

    def test_local_and_ci_selection(self):
        with tempfile.TemporaryDirectory() as directory:
            self.root = Path(directory)
            original = markdown_files.ROOT
            markdown_files.ROOT = self.root
            try:
                self.git("init", "-q")
                self.git("config", "user.email", "test@example.invalid")
                self.git("config", "user.name", "Test")
                self.write("rename.md")
                self.write("deleted.md")
                self.write("unstaged.md")
                self.write("other.txt")
                self.git("add", ".")
                self.git("commit", "-qm", "base")
                self.git("mv", "rename.md", "renamed file.md")
                self.git("rm", "-q", "deleted.md")
                self.write("new file.md")
                self.write("unstaged.md", "変更。\n")
                self.write("api/build/ignored.md")
                self.write("other.txt", "changed")
                local = {p.decode() for p in markdown_files.selected("local", None)
                         if markdown_files.included(p)}
                self.assertIn("renamed file.md", local)
                self.assertIn("new file.md", local)
                self.assertIn("unstaged.md", local)
                self.assertNotIn("deleted.md", local)
                self.assertNotIn("other.txt", local)
                self.assertNotIn("api/build/ignored.md", local)
                self.git("add", "new file.md", "unstaged.md", "other.txt")
                self.git("commit", "-qm", "change")
                ci = {p.decode() for p in markdown_files.selected("ci", "HEAD^")
                      if markdown_files.included(p)}
                self.assertIn("renamed file.md", ci)
                self.assertIn("new file.md", ci)
                self.assertIn("unstaged.md", ci)
                self.assertNotIn("deleted.md", ci)
                self.assertEqual({p.decode() for p in markdown_files.selected("local", None)
                                  if markdown_files.included(p)}, set())
                self.assertEqual(markdown_files.selected("ci", "HEAD"), [])
            finally:
                markdown_files.ROOT = original


if __name__ == "__main__":
    unittest.main()
