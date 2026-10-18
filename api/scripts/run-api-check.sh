#!/usr/bin/env bash

set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly API_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
readonly REPO_ROOT="$(cd "${API_DIR}/.." && pwd)"

exec "${REPO_ROOT}/scripts/run-gate-with-log.sh" api check -- \
    "${API_DIR}/gradlew" -p "${API_DIR}" check --console=plain
