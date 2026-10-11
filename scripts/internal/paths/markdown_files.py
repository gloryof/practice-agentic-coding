#!/usr/bin/env python3
"""Return selected Markdown paths as NUL-delimited repository-relative names."""

import argparse
import os
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parent.parent.parent.parent
EXCLUDED = ("api/build/", ".idea/", ".git/", "frontend/node_modules/")


def git(*args):
    return subprocess.check_output(["git", *args], cwd=ROOT).split(b"\0")[:-1]


def selected(mode, base):
    if mode == "all":
        return git("ls-files", "-z", "--cached", "--others", "--exclude-standard")
    if mode == "ci":
        if not base:
            raise ValueError("--base is required for --ci")
        return git("diff", "--name-only", "-z", "--diff-filter=ACMR", base, "HEAD")
    paths = set(git("diff", "--name-only", "-z", "--diff-filter=ACMR"))
    paths.update(git("diff", "--cached", "--name-only", "-z", "--diff-filter=ACMR"))
    paths.update(git("ls-files", "--others", "--exclude-standard", "-z"))
    return paths


def included(raw):
    name = os.fsdecode(raw)
    path = ROOT / name
    return (name.lower().endswith(".md") and not name.startswith(EXCLUDED)
            and path.is_file() and not path.is_symlink())


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group()
    group.add_argument("--all", action="store_true")
    group.add_argument("--ci", action="store_true")
    parser.add_argument("--base", help="CI comparison commit or ref")
    args = parser.parse_args()
    mode = "all" if args.all else "ci" if args.ci else "local"
    try:
        paths = selected(mode, args.base)
    except (ValueError, subprocess.CalledProcessError) as exc:
        parser.error(str(exc))
    for raw in sorted(set(paths)):
        if included(raw):
            sys.stdout.buffer.write(raw + b"\0")


if __name__ == "__main__":
    main()
