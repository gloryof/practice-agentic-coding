#!/usr/bin/env bash
set -euo pipefail

printf '%s\n' '[performance] 検証用のローカルagenticデータベースを削除して再作成します。'
mkdir -p test-results
OPENAPI_ENABLED=true ./scripts/start-e2e-api.sh > test-results/performance-api.log 2>&1 &
api_process_pid=$!
api_listener_pid=""

cleanup() {
  if [[ -n "${api_listener_pid}" ]]; then
    kill "${api_listener_pid}" 2>/dev/null || true
  fi
  kill "${api_process_pid}" 2>/dev/null || true
  wait "${api_process_pid}" 2>/dev/null || true
  ../.codex/skills/run-api-e2e/scripts/run.sh stop
}
trap cleanup EXIT INT TERM

for attempt in {1..120}; do
  if ! kill -0 "${api_process_pid}" 2>/dev/null; then
    cat test-results/performance-api.log >&2
    exit 1
  fi
  if curl -fsS --max-time 2 http://127.0.0.1:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    api_listener_pid="$(lsof -nP -iTCP:8080 -sTCP:LISTEN -t | head -n 1)"
    break
  fi
  sleep 1
done

[[ -n "${api_listener_pid}" ]] || { cat test-results/performance-api.log >&2; exit 1; }
npx lhci autorun
