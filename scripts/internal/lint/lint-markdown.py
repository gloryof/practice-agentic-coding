#!/usr/bin/env python3
"""Lint Japanese sentence boundaries in Markdown prose (MDJ001)."""

import argparse
from pathlib import Path
import re
import sys

FENCE = re.compile(r"^ {0,3}(`{3,}|~{3,})")
LIST = re.compile(r"^(\s*(?:[-+*]|\d+[.)])\s+)")
QUOTE = re.compile(r"^(\s*>\s*)+")
URL = re.compile(r"(?:https?://|file:///)[^\s<>。]+")
AUTOLINK = re.compile(r"<[^>\n]+>")
CLOSERS = "」』）)]}】〉》”’\"'*_~"


def prose_lines(source):
    """Yield source lines belonging to prose blocks in a small Markdown block AST."""
    fence_char = None
    fence_size = 0
    html_end = None
    frontmatter = bool(source and source[0].strip() == "---")
    for number, line in enumerate(source, 1):
        raw = line.rstrip("\r\n")
        if frontmatter:
            if number > 1 and raw.strip() in ("---", "..."):
                frontmatter = False
            continue
        if html_end:
            if html_end in raw:
                html_end = None
            continue
        unquoted = QUOTE.sub("", raw)
        marker = FENCE.match(unquoted)
        if fence_char:
            if marker and marker.group(1)[0] == fence_char and len(marker.group(1)) >= fence_size:
                fence_char = None
            continue
        if marker:
            fence_char, fence_size = marker.group(1)[0], len(marker.group(1))
            continue
        if not raw.strip():
            continue
        if raw.startswith(("    ", "\t")) and not LIST.match(raw):
            continue
        stripped = raw.lstrip().lower()
        for opening, closing in (("<!--", "-->"), ("<script", "</script>"),
                                 ("<style", "</style>"), ("<pre", "</pre>")):
            if stripped.startswith(opening):
                if closing not in stripped:
                    html_end = closing
                break
        else:
            opening = None
        if opening or stripped.startswith("<!doctype"):
            continue
        # GFM table rows and separator rows are outside the prose rule.
        if raw.lstrip().startswith("|") or re.search(r"(?<!\\)\s\|\s", raw):
            continue
        yield number, raw


def visible(line):
    """Mask inline code, link destinations, autolinks and URLs; retain labels."""
    chars = list(line)

    def mask(start, end):
        chars[start:end] = " " * (end - start)

    pos = 0
    while pos < len(line):
        if line[pos] == "`":
            end = pos + 1
            while end < len(line) and line[end] == "`":
                end += 1
            marker = line[pos:end]
            close = line.find(marker, end)
            if close != -1:
                mask(pos, close + len(marker))
                pos = close + len(marker)
                continue
        pos += 1
    for match in AUTOLINK.finditer(line):
        mask(*match.span())
    for match in URL.finditer(line):
        mask(*match.span())
    # Parenthesized link targets can contain balanced parentheses.
    pos = 0
    while pos < len(line) - 1:
        if line[pos:pos + 2] == "](":
            depth = 1
            end = pos + 2
            while end < len(line) and depth:
                if line[end] == "(" and line[end - 1] != "\\":
                    depth += 1
                elif line[end] == ")" and line[end - 1] != "\\":
                    depth -= 1
                end += 1
            if depth == 0:
                mask(pos + 1, end)
                pos = end
                continue
        if line[pos:pos + 2] == "][":
            end = line.find("]", pos + 2)
            if end != -1:
                mask(pos + 1, end + 1)
                pos = end + 1
                continue
        pos += 1
    return "".join(chars)


def violation(line):
    shown = visible(line)
    for index, char in enumerate(shown):
        if char != "。":
            continue
        end = index + 1
        while end < len(shown) and shown[end] in CLOSERS:
            end += 1
        if shown[end:].strip():
            return index, end
    return None


def lint(path):
    lines = path.read_text(encoding="utf-8").splitlines(keepends=True)
    failures = []
    for number, line in prose_lines(lines):
        found = violation(line)
        if found:
            failures.append(number)
    return failures


def fix(path):
    """Split safe prose lines without changing block or inline Markdown boundaries."""
    lines = path.read_text(encoding="utf-8").splitlines(keepends=True)
    eligible = {number for number, _ in prose_lines(lines)}
    output = []
    for number, original in enumerate(lines, 1):
        if number not in eligible:
            output.append(original)
            continue
        line = original.rstrip("\r\n")
        if line.lstrip().startswith(("#", "<")):
            output.append(original)
            continue
        prefix = ""
        quote = QUOTE.match(line)
        listed = LIST.match(line)
        if quote:
            prefix = quote.group(0)
        elif listed:
            prefix = " " * len(listed.group(0))
        fragments = []
        while found := violation(line):
            _, end = found
            # An open link label or inline HTML must remain on one line.
            if line[:end].count("[") > line[:end].count("]") or "<" in line[:end]:
                break
            fragments.append(line[:end].rstrip())
            line = prefix + line[end:].lstrip()
        if fragments:
            newline = "\r\n" if original.endswith("\r\n") else "\n"
            output.append(newline.join([*fragments, line]) + newline)
        else:
            output.append(original)
    changed = "".join(output) != "".join(lines)
    if changed:
        path.write_text("".join(output), encoding="utf-8")
    return changed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fix", action="store_true", help="split safe prose lines")
    parser.add_argument("paths", nargs="+", type=Path)
    args = parser.parse_args()
    failed = False
    for path in args.paths:
        if not path.is_file():
            parser.error(f"not a file: {path}")
        if args.fix:
            fix(path)
        for number in lint(path):
            print(f"{path}:{number}: MDJ001: 句点「。」の後で改行してください。")
            failed = True
    return int(failed)


if __name__ == "__main__":
    sys.exit(main())
