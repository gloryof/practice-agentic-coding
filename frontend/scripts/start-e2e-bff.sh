#!/usr/bin/env bash
set -euo pipefail

mkdir -p test-results
export REGISTRATION_REDIS_URL="${REGISTRATION_REDIS_URL:-redis://127.0.0.1:6379}"
export REGISTRATION_LIMIT_HMAC_KEY="${REGISTRATION_LIMIT_HMAC_KEY:-test-only-registration-key-with-32-bytes}"
export REGISTRATION_LIMIT_GLOBAL_MAX="${REGISTRATION_LIMIT_GLOBAL_MAX:-100}"
exec npm run start > test-results/bff.log 2>&1
