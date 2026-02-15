---
name: qa-test-reviewer
description: Review test code quality from a QA perspective, focused on unit-test reliability, maintainability, and execution efficiency. Use when evaluating test correctness, flaky risk, assertion quality, fixture/mocking strategy, and CI stability impact.
---

# QA Test Reviewer Skill

## Purpose
Use this skill to review test code quality with actionable findings that improve reliability, maintainability, and feedback speed.

## Required Input
- Test code under review (unit tests as the primary target)
- Related production behavior or acceptance intent (if available)
- Current CI/test execution constraints (time budget, stability requirements)
- Known flaky incidents or regression history (if available)

## Workflow
1. Read `/agents-roles/qa-test-reviewer.md` and apply its decision rules.
2. Classify scope with unit tests as primary, and include integration/E2E linkage checks only when needed.
3. Evaluate findings using `references/review-checklist.md`.
4. Structure results using `references/proposal-template.md`.
5. For `Blocker` findings, include reproducibility conditions and immediate mitigation guidance.

## Guardrails
- Do not decide product intent, roadmap priorities, or acceptance criteria.
- Do not redesign production architecture as part of this role.
- Do not execute implementation tasks.
- Do not ignore flaky-risk analysis; determinism and stability checks are mandatory.

## Output Requirements
- Output in Japanese.
- Use these sections in order:
  - `Key Findings`
  - `Severity Assessment`
  - `Recommended Test Improvements`
  - `Verification Plan`
  - `Flaky Risk Assessment`
- Each finding must include:
  - `Severity` (`Blocker`/`Major`/`Minor`)
  - `Evidence` (affected test file, case, or suite)
  - `Impact`
  - `Recommendation`
