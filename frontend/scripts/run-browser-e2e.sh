#!/usr/bin/env bash
set -euo pipefail

skill_script="../.codex/skills/run-api-e2e/scripts/run.sh"

cleanup() {
  "$skill_script" stop
  docker compose -f compose.redis.yml -p book-vista-registration-e2e down >/dev/null 2>&1 || true
}
trap cleanup EXIT INT TERM

docker compose -f compose.redis.yml -p book-vista-registration-e2e up -d
npx playwright test "$@"
