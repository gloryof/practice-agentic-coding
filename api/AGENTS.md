# AGENTS.md for `api/`

## Scope
- This file applies to all work under `api/`.
- Follow this file before the repository root `AGENTS.md` when working in `api/`.

## Mandatory Rule
- `MUST` read and follow `api/docs/architecture.md` before implementing or reviewing API-related changes.
- `MUST` run `./gradlew ktlintFormat` before finalizing Kotlin changes.
- `MUST` run `./gradlew check` before sharing implementation or review results.
- `MUST` stop and propose a rules update first if implementation needs to violate the coding rules.

## Priority Checkpoints (from coding rules)
- `MUST` verify Command/Query architecture and API rules per `api/docs/architecture.md`.

## Review Expectations
- `MUST` include a brief compliance note in API implementation/review output, stating which rules were checked.
- `MUST` flag dependency-direction violations and HTTP method misuse as blocking issues.
- `MUST` ensure UnitTest changes are reviewed with the QA Test Reviewer perspective (`qa-test-reviewer`) before finalizing API work.
- `SHOULD` run `rg -n "throw " api/src/main/kotlin` and verify throws are only in Web layer for business control.
- `SHOULD` verify domain packages use `model/event/service/repository` classification in command contexts.

## Exception Handling
- If an exception is unavoidable, document:
- reason and necessity
- impact scope
- mitigation and resolution plan
- target timing for review/removal
