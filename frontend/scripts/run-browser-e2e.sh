#!/usr/bin/env bash
set -euo pipefail

skill_script="../.codex/skills/run-api-e2e/scripts/run.sh"

cleanup() {
  "$skill_script" stop
}
trap cleanup EXIT INT TERM

npx playwright test "$@"
