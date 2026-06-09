#!/usr/bin/env bash

set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "${SCRIPT_DIR}/../../../.." && pwd)"
readonly API_DIR="${REPO_ROOT}/api"
readonly COMPOSE_FILE="${API_DIR}/docker-compose.yml"
readonly CONTAINER_NAME="agentic-postgres"
readonly COMPOSE_PROJECT="api"
readonly COMPOSE_SERVICE="postgres"
readonly API_BASE_URL="http://localhost:8080"
readonly HEALTH_URL="${API_BASE_URL}/actuator/health"
readonly LOG_DIR="${API_DIR}/build/run-api-e2e"
readonly API_LOG="${LOG_DIR}/api.log"

API_PROCESS_PID=""
API_LISTENER_PID=""

log() {
    printf '[run-api-e2e] %s\n' "$*"
}

fail() {
    printf '[run-api-e2e] ERROR: %s\n' "$*" >&2
    exit 1
}

usage() {
    cat <<'EOF'
Usage: run.sh <start|e2e|stop>

  start  Recreate the local database, migrate, seed, and run the API.
  e2e    Recreate the local database, run the API and E2E tests, then clean up.
  stop   Stop and remove the skill-managed PostgreSQL service.

WARNING: start and e2e delete and recreate the local agentic database.
EOF
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

check_docker_prerequisites() {
    require_command docker
    docker version >/dev/null 2>&1 || fail "Docker daemon is unavailable. Start Docker and retry."
    docker compose version >/dev/null 2>&1 || fail "Docker Compose is unavailable."
}

check_workflow_prerequisites() {
    check_docker_prerequisites
    require_command curl
    require_command lsof
    require_command java
    [[ -x "${API_DIR}/gradlew" ]] || fail "Gradle wrapper is not executable: api/gradlew"
}

listener_pid() {
    lsof -nP -iTCP:8080 -sTCP:LISTEN -t 2>/dev/null | head -n 1
}

assert_api_port_available() {
    local pid
    pid="$(listener_pid || true)"
    [[ -z "${pid}" ]] || fail "Port 8080 is already in use by PID ${pid}. Stop that process before retrying."
}

container_id() {
    docker ps -aq --filter "name=^/${CONTAINER_NAME}$" | head -n 1
}

assert_compose_ownership() {
    local id project service
    id="$(container_id)"
    [[ -n "${id}" ]] || return 0

    project="$(docker inspect --format '{{ index .Config.Labels "com.docker.compose.project" }}' "${id}")"
    service="$(docker inspect --format '{{ index .Config.Labels "com.docker.compose.service" }}' "${id}")"
    if [[ "${project}" != "${COMPOSE_PROJECT}" || "${service}" != "${COMPOSE_SERVICE}" ]]; then
        fail "Container ${CONTAINER_NAME} is not owned by Compose project ${COMPOSE_PROJECT}/${COMPOSE_SERVICE}; refusing to modify it."
    fi
}

compose_down() {
    assert_compose_ownership
    log "Stopping and removing PostgreSQL."
    docker compose -f "${COMPOSE_FILE}" down --volumes --remove-orphans
}

prepare_database() {
    log "Recreating the local agentic database. Existing local data will be deleted."
    compose_down
    docker compose -f "${COMPOSE_FILE}" up -d --wait "${COMPOSE_SERVICE}"

    log "Applying Flyway migrations."
    "${API_DIR}/gradlew" -p "${API_DIR}" flywayMigrate --console=plain

    log "Loading the standard E2E seed."
    docker exec -i "${CONTAINER_NAME}" \
        psql -v ON_ERROR_STOP=1 -U agentic -d agentic \
        < "${API_DIR}/scripts/db/book_item_data.sql"
}

wait_for_api() {
    local attempts=60
    local health

    log "Waiting for API health at ${HEALTH_URL}."
    for ((attempt = 1; attempt <= attempts; attempt++)); do
        if [[ -n "${API_PROCESS_PID}" ]] && ! kill -0 "${API_PROCESS_PID}" 2>/dev/null; then
            fail "API process exited before becoming healthy. See api/build/run-api-e2e/api.log."
        fi

        health="$(curl -fsS --max-time 2 "${HEALTH_URL}" 2>/dev/null || true)"
        if [[ "${health}" == *'"status":"UP"'* ]]; then
            API_LISTENER_PID="$(listener_pid || true)"
            [[ -n "${API_LISTENER_PID}" ]] || fail "API is healthy but its listener PID could not be identified."
            log "API health is UP."
            return 0
        fi
        sleep 1
    done

    fail "API did not become healthy within ${attempts} seconds. See api/build/run-api-e2e/api.log."
}

start_api_in_background() {
    mkdir -p "${LOG_DIR}"
    : > "${API_LOG}"
    log "Starting API. Log: api/build/run-api-e2e/api.log"
    (
        cd "${API_DIR}"
        ./gradlew bootRun --console=plain
    ) > "${API_LOG}" 2>&1 &
    API_PROCESS_PID=$!
}

stop_background_api() {
    if [[ -n "${API_LISTENER_PID}" ]] && kill -0 "${API_LISTENER_PID}" 2>/dev/null; then
        log "Stopping API listener PID ${API_LISTENER_PID}."
        kill "${API_LISTENER_PID}" 2>/dev/null || true
    fi

    if [[ -n "${API_PROCESS_PID}" ]] && kill -0 "${API_PROCESS_PID}" 2>/dev/null; then
        kill "${API_PROCESS_PID}" 2>/dev/null || true
        wait "${API_PROCESS_PID}" 2>/dev/null || true
    fi
    API_PROCESS_PID=""
    API_LISTENER_PID=""
}

cleanup_e2e() {
    local exit_code="${1:-$?}"
    trap - EXIT INT TERM
    stop_background_api
    compose_down || true
    if [[ ${exit_code} -eq 0 ]]; then
        log "E2E cleanup completed."
    else
        log "E2E failed with exit code ${exit_code}. See api/build/run-api-e2e/api.log." >&2
    fi
    exit "${exit_code}"
}

run_start() {
    check_workflow_prerequisites
    assert_api_port_available
    prepare_database
    log "Starting API in the foreground. Press Ctrl-C to stop it; run 'run.sh stop' to remove PostgreSQL."
    cd "${API_DIR}"
    exec ./gradlew bootRun --console=plain
}

run_e2e() {
    check_workflow_prerequisites
    assert_api_port_available
    trap 'cleanup_e2e $?' EXIT
    trap 'cleanup_e2e 130' INT
    trap 'cleanup_e2e 143' TERM
    prepare_database
    start_api_in_background
    wait_for_api

    log "Running E2E tests."
    env API_BASE_URL="${API_BASE_URL}" \
        "${API_DIR}/gradlew" -p "${API_DIR}" e2eTest --rerun-tasks --console=plain
    log "E2E tests passed."
}

run_stop() {
    check_docker_prerequisites
    compose_down
    log "PostgreSQL cleanup completed."
}

main() {
    case "${1:-}" in
        start)
            run_start
            ;;
        e2e)
            run_e2e
            ;;
        stop)
            run_stop
            ;;
        -h|--help|help)
            usage
            ;;
        *)
            usage >&2
            exit 2
            ;;
    esac
}

main "$@"
