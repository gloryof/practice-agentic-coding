#!/usr/bin/env python3
"""Regression tests for Markdown syntax exclusions and sentence splitting."""

import importlib.util
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parent.parent / "lint/lint-markdown.py"
spec = importlib.util.spec_from_file_location("lint_markdown", SCRIPT)
lint_markdown = importlib.util.module_from_spec(spec)
spec.loader.exec_module(lint_markdown)


class MarkdownLintTest(unittest.TestCase):
    def test_prose_and_exclusions(self):
        source = """---
description: 例。続く
---
本文。次文
`例。続く` は対象外。
```md
コード。続く
```
    インデント。続く
| 列 | 内容。続く |
| --- | --- |
リンク[表示](https://example.com/。path)は対象外。
リンク[表示](https://example.com/a(b。c))も対象外。
引用「文章。」次文
    - 入れ子。次文
URL https://example.com。次文
> ```
> 引用コード。続く
> ```
<!--
コメント。続く
-->
"""
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "sample.md"
            path.write_text(source, encoding="utf-8")
            self.assertEqual(lint_markdown.lint(path), [4, 14, 15, 16])

    def test_fix_preserves_list_and_code(self):
        source = "- 最初。次文\n```\nコード。続く\n```\n"
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "sample.md"
            path.write_text(source, encoding="utf-8")
            self.assertTrue(lint_markdown.fix(path))
            self.assertEqual(path.read_text(encoding="utf-8"),
                             "- 最初。\n  次文\n```\nコード。続く\n```\n")

    def test_cli_reports_file_line_and_rule(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "sample.md"
            path.write_text("# 見出し\n本文。次文\n", encoding="utf-8")
            result = subprocess.run([sys.executable, str(SCRIPT), str(path)],
                                    capture_output=True, text=True, check=False)
            self.assertEqual(result.returncode, 1)
            self.assertIn(f"{path}:2: MDJ001:", result.stdout)


if __name__ == "__main__":
    unittest.main()
