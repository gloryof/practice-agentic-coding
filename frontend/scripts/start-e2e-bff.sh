#!/usr/bin/env bash
set -euo pipefail

mkdir -p test-results
exec npm run start > test-results/bff.log 2>&1
