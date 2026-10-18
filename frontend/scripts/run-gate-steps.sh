#!/usr/bin/env bash

set -euo pipefail

gate="${1:-}"
[[ $# -gt 0 ]] && shift

case "${gate}" in
    check)
        [[ $# -eq 0 ]] || exit 2
        npm run format:check
        npm run typecheck
        npm run lint
        npm run test
        npm run build
        npm run build:storybook
        npm run test:storybook
        ;;
    e2e)
        npm run build
        ./scripts/run-browser-e2e.sh --project=chromium "$@"
        ;;
    e2e-cross-browser)
        npm run build
        ./scripts/run-browser-e2e.sh "$@"
        ;;
    performance)
        [[ $# -eq 0 ]] || exit 2
        npm run build
        ./scripts/run-performance.sh
        ;;
    *)
        printf '不明なゲート: %s\n' "${gate}" >&2
        exit 2
        ;;
esac
