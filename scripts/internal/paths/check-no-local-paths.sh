#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
cd "$ROOT_DIR"

if [[ "${1:-}" == "--all" ]]; then
  files=()
  while IFS= read -r -d '' file; do
    files+=("$file")
  done < <(python3 scripts/internal/paths/markdown_files.py --all)
  shift
  if (( $# )); then
    echo "ERROR: --all cannot be combined with paths." >&2
    exit 2
  fi
elif (( $# )); then
  files=("$@")
else
  echo "ERROR: Specify Markdown paths or --all." >&2
  exit 2
fi

if (( ${#files[@]} == 0 )); then
  echo "OK: No Markdown files to check."
  exit 0
fi

for file in "${files[@]}"; do
  if [[ "$file" != *.[mM][dD] || ! -f "$file" ]]; then
    echo "ERROR: Expected an existing Markdown file: $file" >&2
    exit 2
  fi
done

if ! command -v rg >/dev/null 2>&1; then
  echo "ERROR: ripgrep (rg) is required." >&2
  exit 2
fi

pattern='(/Users/[A-Za-z0-9_-][^[:space:]`"]*|/home/[A-Za-z0-9_-][^[:space:]`"]*|file:///[A-Za-z0-9]|[A-Za-z]:\\Users\\[A-Za-z0-9_-][^[:space:]`"]*)'

if matches=$(rg -n --pcre2 "$pattern" -- "${files[@]}"); then
  echo "ERROR: Found machine-local absolute paths in Markdown files:" >&2
  printf '%s\n' "$matches" >&2
  exit 1
else
  status=$?
  if (( status != 1 )); then
    exit "$status"
  fi
fi

echo "OK: No machine-local absolute paths found in Markdown files."
