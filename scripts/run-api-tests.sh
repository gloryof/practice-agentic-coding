#!/usr/bin/env bash

set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
readonly API_DIR="${REPO_ROOT}/api"

usage() {
    cat <<'EOF'
使用方法:
  ./scripts/run-api-tests.sh
  ./scripts/run-api-tests.sh --tests <クラス・メソッド・ワイルドカード>
EOF
}

run_all_tests() {
    exec "${API_DIR}/gradlew" -p "${API_DIR}" test --console=plain
}

run_filtered_tests() {
    local filter="$1"
    exec "${API_DIR}/gradlew" -p "${API_DIR}" test --console=plain --tests "${filter}"
}

case "$#" in
    0)
        run_all_tests
        ;;
    2)
        if [[ "$1" != "--tests" || -z "$2" ]]; then
            usage >&2
            exit 2
        fi
        run_filtered_tests "$2"
        ;;
    *)
        usage >&2
        exit 2
        ;;
esac
