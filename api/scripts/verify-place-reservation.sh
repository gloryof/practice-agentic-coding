#!/usr/bin/env bash

set -euo pipefail

readonly API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
readonly BOOK_PRODUCT_ID="${BOOK_PRODUCT_ID:-book-0003}"
readonly EXPECTED_TITLE="${EXPECTED_TITLE:-世界史への招待}"
readonly EXPECTED_ISBN="${EXPECTED_ISBN:-9780000000003}"
readonly PASSWORD="Passw0rd!123456"

EMAIL="reservation-smoke-$(date +%s)-$$@example.com"
ACCESS_TOKEN=""
RESPONSE_BODY=""
HTTP_STATUS=""

fail() {
    printf '[verify-place-reservation] ERROR: %s\n' "$*" >&2
    if [[ -n "${RESPONSE_BODY}" ]]; then
        printf '[verify-place-reservation] Last response body:\n%s\n' "${RESPONSE_BODY}" >&2
    fi
    exit 1
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

request_json() {
    local method="$1"
    local path="$2"
    local body="$3"
    local token="${4:-}"
    local response_file

    response_file="$(mktemp)"
    if [[ -n "${token}" ]]; then
        HTTP_STATUS="$(
            curl -sS -o "${response_file}" -w '%{http_code}' \
                -X "${method}" \
                -H 'Content-Type: application/json' \
                -H 'Accept: application/json' \
                -H "Authorization: Bearer ${token}" \
                --data "${body}" \
                "${API_BASE_URL%/}${path}"
        )"
    else
        HTTP_STATUS="$(
            curl -sS -o "${response_file}" -w '%{http_code}' \
                -X "${method}" \
                -H 'Content-Type: application/json' \
                -H 'Accept: application/json' \
                --data "${body}" \
                "${API_BASE_URL%/}${path}"
        )"
    fi
    RESPONSE_BODY="$(cat "${response_file}")"
    rm -f "${response_file}"
}

assert_status() {
    local expected="$1"
    [[ "${HTTP_STATUS}" == "${expected}" ]] || fail "Expected HTTP ${expected}, got ${HTTP_STATUS}."
}

assert_body_contains() {
    local expected="$1"
    [[ "${RESPONSE_BODY}" == *"${expected}"* ]] || fail "Expected response body to contain: ${expected}"
}

extract_access_token() {
    ACCESS_TOKEN="$(
        printf '%s' "${RESPONSE_BODY}" |
            sed -n 's/.*"access_token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p'
    )"
    [[ -n "${ACCESS_TOKEN}" ]] || fail "access_token was not returned."
}

main() {
    require_command curl
    require_command sed
    require_command mktemp

    printf '[verify-place-reservation] API_BASE_URL=%s\n' "${API_BASE_URL}"
    printf '[verify-place-reservation] Registering user %s\n' "${EMAIL}"
    request_json \
        POST \
        /api/v1/library-users/registrations \
        "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}"
    assert_status 201
    assert_body_contains "\"email\":\"${EMAIL}\""

    printf '[verify-place-reservation] Logging in\n'
    request_json \
        POST \
        /api/v1/auth/login \
        "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}"
    assert_status 200
    extract_access_token

    printf '[verify-place-reservation] Placing reservation for %s\n' "${BOOK_PRODUCT_ID}"
    request_json \
        POST \
        /api/v1/reservations \
        "{\"book_product_id\":\"${BOOK_PRODUCT_ID}\"}" \
        "${ACCESS_TOKEN}"
    assert_status 201
    assert_body_contains "\"book_product_id\":\"${BOOK_PRODUCT_ID}\""
    assert_body_contains "\"title\":\"${EXPECTED_TITLE}\""
    assert_body_contains "\"isbn\":\"${EXPECTED_ISBN}\""
    assert_body_contains "\"event_name\":\"ReservationPlacedEvent\""

    printf '[verify-place-reservation] Reservation flow verified.\n'
}

main "$@"
