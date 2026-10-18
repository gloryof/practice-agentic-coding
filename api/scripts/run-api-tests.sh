#!/usr/bin/env bash

set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly API_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
readonly REPO_ROOT="$(cd "${API_DIR}/.." && pwd)"

usage() {
    cat <<'EOF'
使用方法:
  ./api/scripts/run-api-tests.sh
  ./api/scripts/run-api-tests.sh --tests <クラス・メソッド・ワイルドカード>
EOF
}

case "$#" in
    0)
        exec "${REPO_ROOT}/scripts/run-gate-with-log.sh" api test -- \
            "${API_DIR}/gradlew" -p "${API_DIR}" test --console=plain
        ;;
    2)
        if [[ "$1" != "--tests" || -z "$2" ]]; then
            usage >&2
            exit 2
        fi
        exec "${REPO_ROOT}/scripts/run-gate-with-log.sh" api test -- \
            "${API_DIR}/gradlew" -p "${API_DIR}" test --console=plain --tests "$2"
        ;;
    *)
        usage >&2
        exit 2
        ;;
esac
