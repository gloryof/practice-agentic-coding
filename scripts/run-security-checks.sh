#!/usr/bin/env bash

set -uo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly CHECK_DIR="${SCRIPT_DIR}/internal/security"

if [[ $# -ne 2 || "$1" != --group ]]; then
    printf '使用方法: %s --group <static|dynamic>\n' "$0" >&2
    exit 2
fi

case "$2" in
    static)
        checks=(npm-audit java-dependencies container-images source secrets)
        ;;
    dynamic)
        checks=(zap scenarios)
        ;;
    *)
        printf '不明な検査グループ: %s\n' "$2" >&2
        exit 2
        ;;
esac

result=0
for check in "${checks[@]}"; do
    script="${CHECK_DIR}/check-${check}.sh"
    if [[ -f "${script}" ]]; then
        bash "${script}"
        status=$?
    else
        printf '%s: 未実装\n' "${check}"
        status=2
    fi
    case "${status}" in
        0) label='完了・対応が必要な指摘なし' ;;
        1) label='完了・対応が必要な指摘あり' ;;
        *) label='失敗・未完了'; status=2 ;;
    esac
    printf '%s: %s（終了コード: %s）\n' "${check}" "${label}" "${status}"
    if (( status > result )); then result=${status}; fi
done
printf 'グループ %s: 終了コード %s（0=指摘なし、1=指摘あり、2=失敗・未完了）\n' "$2" "${result}"
exit "${result}"
