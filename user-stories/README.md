# User Stories Operating Guide

## Purpose
- This directory manages product tasks as one story per file.
- It is designed for agentic delivery from specification to implementation.

## File Unit and Naming
- Use one file per story.
- File name format: `US-XXXX-<short-title>.md`.
- Start from `US-0001` and keep the ID stable after creation.
- If a story is deprecated, keep the file and mark it in `Notes` instead of deleting it.

## Status Model
- `Todo`: drafted and not started.
- `InProgress`: accepted for implementation.
- `Done`: accepted criteria and checks are completed.

## Required Sections in Each Story
- `Status`
- `User Story`
- `User Benefit`
- `Acceptance Criteria`
- `Scope`
- `API Impact`
- `Data Impact`
- `Test Scenarios`
- `Dependencies`
- `Notes`

## Gate Rules
- Move to `InProgress` only when:
- Acceptance criteria are testable and complete.
- `API Impact` and `Test Scenarios` are filled (`None` is allowed).
- Move to `Done` only when:
- Acceptance criteria are covered by tests or explicit verification notes.
- API coding rules compliance is confirmed for `api/` changes, or exceptions are documented.
- Remaining issues are listed in `Notes` (`None` if empty).

## Routing
- Use `po-story` for writing and refining stories.
- Use `po-spec` for product intent, requirements, and acceptance criteria decisions.
- Use engineering review skills as needed:
- `qa-test-reviewer`
- `security-engineer-reviewer`
- `server-architecture-reviewer`
- For any work under `api/`, follow `api/AGENTS.md` and `api/docs/coding-rules.md`.
