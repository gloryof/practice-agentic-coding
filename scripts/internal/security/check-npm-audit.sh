#!/usr/bin/env bash

set -uo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

if [[ $# -ne 0 ]]; then
    printf '使用方法: %s\n' "$0" >&2
    exit 2
fi

docker compose --project-directory "${REPO_ROOT}" --project-name security-check \
    --file "${REPO_ROOT}/compose.security.yml" run --rm --no-deps -T npm-audit
status=$?
# 指摘はコンテナ内で10とし、Compose自身の終了コード1と区別する。
case "${status}" in
    0) exit 0 ;;
    10) exit 1 ;;
    *)
        printf 'npm-audit: 失敗・未完了（Compose終了コード: %s）\n' "${status}" >&2
        exit 2
        ;;
esac
