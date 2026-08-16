#!/usr/bin/env bash
set -euo pipefail

mkdir -p test-results
exec ../.codex/skills/run-api-e2e/scripts/run.sh start > test-results/api.log 2>&1
