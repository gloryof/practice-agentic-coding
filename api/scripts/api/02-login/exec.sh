#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="http://localhost:8080"

if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required" >&2
  exit 1
fi

response=$(
  curl -sS -X POST "${API_BASE_URL}/api/v1/auth/login" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d @request.json
)

echo "${response}"

token=$(echo "${response}" | jq -r '.access_token // empty')
if [[ -z "${token}" ]]; then
  echo "Failed to extract access_token" >&2
  exit 1
fi

echo "ACCESS_TOKEN=${token}"
