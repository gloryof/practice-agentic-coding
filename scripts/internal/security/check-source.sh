#!/usr/bin/env bash
set -uo pipefail
exec python3 "$(dirname "${BASH_SOURCE[0]}")/semgrep_checks.py" "$@"
