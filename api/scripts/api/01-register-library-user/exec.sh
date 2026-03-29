#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="http://localhost:8080"

response=$(
  curl -sS -X POST "${API_BASE_URL}/api/v1/library-users/registrations" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d @request.json
)

echo "${response}"
