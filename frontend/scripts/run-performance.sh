#!/usr/bin/env bash
set -euo pipefail

printf '%s\n' '[performance] 検証用のローカルagenticデータベースを削除して再作成します。'
mkdir -p test-results
OPENAPI_ENABLED=true ./scripts/start-e2e-api.sh &
api_process_pid=$!
api_listener_pid=""
bff_process_pid=""

cleanup() {
  if [[ -n "${bff_process_pid}" ]]; then
    kill "${bff_process_pid}" 2>/dev/null || true
    wait "${bff_process_pid}" 2>/dev/null || true
  fi
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
    printf '%s\n' '[performance] API起動に失敗しました。ログ: frontend/test-results/api.log' >&2
    exit 1
  fi
  if curl -fsS --max-time 2 http://127.0.0.1:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    api_listener_pid="$(lsof -nP -iTCP:8080 -sTCP:LISTEN -t | head -n 1)"
    break
  fi
  sleep 1
done

[[ -n "${api_listener_pid}" ]] || {
  printf '%s\n' '[performance] APIのヘルスチェックがタイムアウトしました。ログ: frontend/test-results/api.log' >&2
  exit 1
}
npx lhci autorun

SPRING_API_BASE_URL=http://127.0.0.1:8080 \
  BFF_PUBLIC_ORIGIN=http://127.0.0.1:3000 \
  ./node_modules/.bin/next start > test-results/performance-bff.log 2>&1 &
bff_process_pid=$!
for attempt in {1..60}; do
  if ! kill -0 "${bff_process_pid}" 2>/dev/null; then
    printf '%s\n' '[performance] BFF起動に失敗しました。ログ: frontend/test-results/performance-bff.log' >&2
    exit 1
  fi
  if curl -fsS --max-time 2 http://127.0.0.1:3000/api/health >/dev/null 2>&1; then
    break
  fi
  sleep 1
done
curl -fsS --max-time 2 http://127.0.0.1:3000/api/health >/dev/null
node ./scripts/measure-reservation.mjs
