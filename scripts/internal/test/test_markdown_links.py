#!/usr/bin/env python3
"""Regression tests for repository-local Markdown link targets."""

import importlib.util
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

SCRIPT = Path(__file__).resolve().parent.parent / "paths/check-markdown-links.py"
spec = importlib.util.spec_from_file_location("check_markdown_links", SCRIPT)
check_markdown_links = importlib.util.module_from_spec(spec)
spec.loader.exec_module(check_markdown_links)


class MarkdownLinksTest(unittest.TestCase):
    def test_local_targets_and_syntax_exclusions(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            docs = root / "docs"
            docs.mkdir()
            (docs / "exists.md").write_text("# 見出し\n", encoding="utf-8")
            (docs / "space name.png").write_bytes(b"image")
            source = docs / "source.md"
            source.write_text("""---
description: [無視](missing-frontmatter.md)
---
[存在](exists.md#見出し)
![画像](space%20name.png)
[空白](<space name.png>)
[定義済み][ref]
[ref]: exists.md
[外部](https://example.com/missing.md)
[メール](mailto:user@example.com)
[ページ内](#見出し)
[サイトルート](/elsewhere.md)
`[コード](missing-code.md)`
```md
[フェンス](missing-fence.md)
```
| [表内](exists.md) |
[欠落](missing.md "説明")
[大小文字](Exists.md)
[範囲外](../../outside.md)
[bad]: missing-definition.md
""", encoding="utf-8")
            with patch.object(check_markdown_links, "ROOT", root):
                self.assertEqual(check_markdown_links.lint(source), [
                    (18, "missing.md"),
                    (19, "Exists.md"),
                    (20, "../../outside.md"),
                    (21, "missing-definition.md"),
                ])

    def test_code_mask_and_balanced_parentheses(self):
        self.assertEqual(list(check_markdown_links.targets(
            "`[無視](missing.md)` [入れ子 [表示]](a(b)c.md)")), ["a(b)c.md"])


if __name__ == "__main__":
    unittest.main()
