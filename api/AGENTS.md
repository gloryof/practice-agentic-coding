# AGENTS.md for `api/`

## Scope
- This file applies to all work under `api/`.
- Follow this file before the repository root `AGENTS.md` when working in `api/`.

## Mandatory Rule
- `MUST` read `api/docs/coding-rules.md` before implementing or reviewing API-related changes.
- `MUST` align implementation decisions with `api/docs/coding-rules.md`.
- `MUST` stop and propose a rules update first if implementation needs to violate the coding rules.

## Priority Checkpoints (from coding rules)
- `MUST` verify Command architecture rules in section 4.
- `MUST` verify Query architecture rules in section 5.
- `MUST` verify Command API rules in section 6.
- `MUST` verify Query API rules in section 7.
- `MUST` verify Command/Query separated test rules in section 8.

## Review Expectations
- `MUST` include a brief compliance note in API implementation/review output, stating which rules were checked.
- `MUST` flag dependency-direction violations and HTTP method misuse as blocking issues.

## Exception Handling
- If an exception is unavoidable, document:
- reason and necessity
- impact scope
- mitigation and resolution plan
- target timing for review/removal
