#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="http://localhost:8080"

if [[ -z "${ACCESS_TOKEN:-}" ]]; then
  echo "ACCESS_TOKEN is required" >&2
  exit 1
fi

declare -a params=()

TITLE=""
TITLE_KANA=""
PUBLISHER=""
PUBLISHER_KANA=""
AUTHOR_NAME=""
AUTHOR_NAME_KANA=""
ISBN="9780000000001"
TITLE_EXACT=""
TITLE_KANA_EXACT=""
PUBLISHER_EXACT=""
PUBLISHER_KANA_EXACT=""
AUTHOR_EXACT=""
AUTHOR_KANA_EXACT=""

add_param() {
  local key="$1"
  local value="$2"
  if [[ -n "${value}" ]]; then
    params+=("--data-urlencode" "${key}=${value}")
  fi
}

add_param "title" "${TITLE}"
add_param "title_kana" "${TITLE_KANA}"
add_param "publisher" "${PUBLISHER}"
add_param "publisher_kana" "${PUBLISHER_KANA}"
add_param "author_name" "${AUTHOR_NAME}"
add_param "author_name_kana" "${AUTHOR_NAME_KANA}"
add_param "isbn" "${ISBN}"
add_param "title_exact" "${TITLE_EXACT}"
add_param "title_kana_exact" "${TITLE_KANA_EXACT}"
add_param "publisher_exact" "${PUBLISHER_EXACT}"
add_param "publisher_kana_exact" "${PUBLISHER_KANA_EXACT}"
add_param "author_exact" "${AUTHOR_EXACT}"
add_param "author_kana_exact" "${AUTHOR_KANA_EXACT}"

if [[ ${#params[@]} -eq 0 ]]; then
  echo "At least one search criteria is required" >&2
  exit 1
fi

curl -sS -G "${API_BASE_URL}/api/v1/book-items" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Accept: application/json" \
  "${params[@]}"
