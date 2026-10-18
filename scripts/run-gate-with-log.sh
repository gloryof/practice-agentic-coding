#!/usr/bin/env bash

set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

usage() {
    printf '使用方法: %s <frontend|api> <ゲート名> -- <コマンド> [引数...]\n' "$0" >&2
    exit 2
}

[[ $# -ge 4 && "$3" == "--" ]] || usage

area="$1"
gate="$2"
shift 3

case "${area}/${gate}" in
    frontend/check|frontend/e2e|frontend/e2e-cross-browser|frontend/performance|api/check|api/test|api/e2e)
        ;;
    *)
        usage
        ;;
esac

readonly log_path="work/logs/${area}/${gate}.log"
readonly log_file="${REPO_ROOT}/${log_path}"
mkdir -p "$(dirname "${log_file}")"

if "$@" > "${log_file}" 2>&1; then
    printf 'PASS %s（ログ: %s）\n' "${area}/${gate}" "${log_path}"
else
    status=$?
    printf 'FAIL %s（終了コード: %s、ログ: %s）\n' "${area}/${gate}" "${status}" "${log_path}" >&2
    exit "${status}"
fi
