#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="http://localhost:8080"

if [[ -z "${ACCESS_TOKEN:-}" ]]; then
  echo "ACCESS_TOKEN is required" >&2
  exit 1
fi

if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required" >&2
  exit 1
fi

response_file=$(mktemp)
trap 'rm -f "${response_file}"' EXIT

status=$(
  curl -sS -o "${response_file}" -w "%{http_code}" -X POST \
    "${API_BASE_URL}/api/v1/auth/logout" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}"
)

if [[ "${status}" != "204" ]]; then
  cat "${response_file}" >&2
  echo "Expected first logout to return 204, got ${status}" >&2
  exit 1
fi

reuse_status=$(
  curl -sS -o "${response_file}" -w "%{http_code}" -X POST \
    "${API_BASE_URL}/api/v1/auth/logout" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}"
)

reuse_code=$(jq -r '.code // empty' "${response_file}")
if [[ "${reuse_status}" != "401" || "${reuse_code}" != "LOGIN_REQUIRED" ]]; then
  cat "${response_file}" >&2
  echo "Expected token reuse to return 401 LOGIN_REQUIRED" >&2
  exit 1
fi

echo "Logout succeeded and the access token cannot be reused."
