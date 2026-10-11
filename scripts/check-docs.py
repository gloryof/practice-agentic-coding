#!/usr/bin/env python3
"""Check changed Markdown, or explicitly selected Markdown paths."""

import argparse
import os
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parent.parent


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group()
    group.add_argument("--all", action="store_true")
    group.add_argument("--ci", action="store_true")
    parser.add_argument("--base")
    parser.add_argument("--fix", action="store_true", help="apply safe MDJ001 fixes before checking")
    parser.add_argument("paths", nargs="*")
    args = parser.parse_args()
    if args.paths and (args.all or args.ci or args.base):
        parser.error("paths cannot be combined with selection options")
    if args.paths:
        paths = [os.fsencode(p) for p in args.paths]
    else:
        command = [sys.executable, str(ROOT / "scripts/internal/paths/markdown_files.py")]
        if args.all:
            command.append("--all")
        if args.ci:
            command.extend(["--ci", "--base", args.base or ""])
        paths = subprocess.check_output(command, cwd=ROOT).split(b"\0")[:-1]
    if not paths:
        print("OK: No Markdown files to check.")
        return 0
    names = [os.fsdecode(p) for p in paths]
    if any(not name.lower().endswith(".md") for name in names):
        parser.error("only Markdown paths are accepted")
    lint_command = [sys.executable, str(ROOT / "scripts/internal/lint/lint-markdown.py")]
    if args.fix:
        lint_command.append("--fix")
    failed = 0
    for command in (lint_command,
                    [sys.executable, str(ROOT / "scripts/internal/paths/check-markdown-links.py")],
                    [str(ROOT / "scripts/internal/paths/check-no-local-paths.sh")]):
        result = subprocess.run([*command, *names], cwd=ROOT, check=False)
        if result.returncode:
            failed = result.returncode
    return failed


if __name__ == "__main__":
    sys.exit(main())
