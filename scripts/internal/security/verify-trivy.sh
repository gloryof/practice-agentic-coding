#!/usr/bin/env bash
set -uo pipefail
exec python3 "$(dirname "${BASH_SOURCE[0]}")/trivy_checks.py" verify "$@"
