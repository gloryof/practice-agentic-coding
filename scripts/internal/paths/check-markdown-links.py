#!/usr/bin/env python3
"""Check repository-local Markdown link targets in explicitly named files."""

import argparse
import os
from pathlib import Path
import re
import sys
from urllib.parse import unquote, urlsplit

ROOT = Path(__file__).resolve().parents[3]
FENCE = re.compile(r"^ {0,3}(?:>\s*)*(`{3,}|~{3,})")
LIST = re.compile(r"^\s*(?:[-+*]|\d+[.)])\s+")
REFERENCE = re.compile(r"^ {0,3}\[([^]]+)\]:\s*(<[^>]+>|\S+)")


def content_lines(lines):
    """Yield line numbers and source lines outside code and frontmatter blocks."""
    fence = None
    frontmatter = bool(lines and lines[0].strip() == "---")
    comment = False
    for number, raw in enumerate(lines, 1):
        line = raw.rstrip("\r\n")
        if frontmatter:
            if number > 1 and line.strip() in ("---", "..."):
                frontmatter = False
            continue
        if comment:
            if "-->" in line:
                comment = False
            continue
        if line.lstrip().startswith("<!--"):
            comment = "-->" not in line
            continue
        marker = FENCE.match(line)
        if fence:
            if marker and marker.group(1)[0] == fence[0] and len(marker.group(1)) >= fence[1]:
                fence = None
            continue
        if marker:
            fence = (marker.group(1)[0], len(marker.group(1)))
            continue
        if line.startswith(("    ", "\t")) and not LIST.match(line):
            continue
        yield number, line


def mask_code(line):
    chars = list(line)
    pos = 0
    while pos < len(line):
        if line[pos] == "`":
            end = pos + 1
            while end < len(line) and line[end] == "`":
                end += 1
            marker = line[pos:end]
            close = line.find(marker, end)
            if close != -1:
                chars[pos:close + len(marker)] = " " * (close + len(marker) - pos)
                pos = close + len(marker)
                continue
        pos += 1
    return "".join(chars)


def destination(line, opening):
    """Read one inline-link destination after its opening parenthesis."""
    pos = opening + 1
    if pos >= len(line):
        return None
    if line[pos] == "<":
        end = line.find(">", pos + 1)
        return line[pos + 1:end] if end != -1 else None
    start = pos
    depth = 0
    while pos < len(line):
        char = line[pos]
        if char == "\\" and pos + 1 < len(line):
            pos += 2
            continue
        if char == "(":
            depth += 1
        elif char == ")":
            if depth == 0:
                return line[start:pos]
            depth -= 1
        elif char.isspace() and depth == 0:
            return line[start:pos]
        pos += 1
    return None


def targets(line):
    clean = mask_code(line)
    clean = re.sub(r"<!--.*?-->", lambda match: " " * len(match.group()), clean)
    definition = REFERENCE.match(clean)
    if definition:
        target = definition.group(2)
        yield target[1:-1] if target.startswith("<") and target.endswith(">") else target
    pos = 0
    while pos < len(clean):
        if clean[pos] != "[" or (pos and clean[pos - 1] == "\\"):
            pos += 1
            continue
        depth = 1
        end = pos + 1
        while end < len(clean) and depth:
            if clean[end] == "\\" and end + 1 < len(clean):
                end += 2
                continue
            if clean[end] == "[":
                depth += 1
            elif clean[end] == "]":
                depth -= 1
            end += 1
        if depth == 0 and end < len(clean) and clean[end] == "(":
            target = destination(line, end)
            if target is not None:
                yield target
        pos = max(end, pos + 1)


def local_path(source, target):
    target = target.replace("\\ ", " ").replace("\\(", "(").replace("\\)", ")")
    if not target or target.startswith(("#", "//")):
        return None
    parsed = urlsplit(target)
    if parsed.scheme or parsed.netloc or parsed.path.startswith("/"):
        return None
    return source.parent / unquote(parsed.path)


def exact_file(path):
    root = ROOT.resolve()
    resolved = path.resolve()
    try:
        parts = resolved.relative_to(root).parts
    except ValueError:
        return False
    current = root
    for part in parts:
        if not current.is_dir() or part not in os.listdir(current):
            return False
        current /= part
    return current.exists()


def lint(path):
    failures = []
    for number, line in content_lines(path.read_text(encoding="utf-8").splitlines()):
        for target in targets(line):
            local = local_path(path, target)
            if local is not None and not exact_file(local):
                failures.append((number, target))
    return failures


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("paths", nargs="+", type=Path)
    args = parser.parse_args()
    failed = False
    for path in args.paths:
        if not path.is_file():
            parser.error(f"not a file: {path}")
        for number, target in lint(path):
            print(f"{path}:{number}: MDL001: リンク先が見つかりません: {target}")
            failed = True
    return int(failed)


if __name__ == "__main__":
    sys.exit(main())
