#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
API_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"
SQLFLUFF_BIN="${API_DIR}/.venv/bin/sqlfluff"
TARGET_PATH="src/main/resources/db/migration"
CONFIG_PATH=".sqlfluff"
ERROR_PLUGIN_LOAD="Failed to load SQLFluff plugin rules"
ERROR_UNKNOWN_RULES="Tried to allowlist unknown rule references"

if [[ ! -x "${SQLFLUFF_BIN}" ]]; then
  echo "sqlfluff binary not found: ${SQLFLUFF_BIN}" >&2
  echo "Run: .venv/bin/pip install -r requirements/sqlfluff.txt" >&2
  exit 1
fi

tmp_output="$(mktemp)"
set +e
(
  cd "${API_DIR}"
  "${SQLFLUFF_BIN}" lint --config "${CONFIG_PATH}" "${TARGET_PATH}"
) 2>&1 | tee "${tmp_output}"
lint_exit_code=${PIPESTATUS[0]}
set -e

if grep -q "${ERROR_PLUGIN_LOAD}" "${tmp_output}" || grep -q "${ERROR_UNKNOWN_RULES}" "${tmp_output}"; then
  rm -f "${tmp_output}"
  echo "Detected SQLFluff plugin loading failure." >&2
  exit 1
fi

rm -f "${tmp_output}"
exit "${lint_exit_code}"
