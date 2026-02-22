#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v rg >/dev/null 2>&1; then
  echo "ERROR: ripgrep (rg) is required." >&2
  exit 2
fi

pattern='(/Users/[A-Za-z0-9_-][^[:space:]`"]*|/home/[A-Za-z0-9_-][^[:space:]`"]*|file:///[A-Za-z0-9]|[A-Za-z]:\\Users\\[A-Za-z0-9_-][^[:space:]`"]*)'

if rg -n --hidden --glob '*.md' --glob '!api/build/**' --glob '!.idea/**' --glob '!.git/**' --pcre2 "$pattern" . >/dev/null; then
  echo "ERROR: Found machine-local absolute paths in Markdown files:" >&2
  rg -n --hidden --glob '*.md' --glob '!api/build/**' --glob '!.idea/**' --glob '!.git/**' --pcre2 "$pattern" . >&2
  exit 1
fi

echo "OK: No machine-local absolute paths found in Markdown files."
