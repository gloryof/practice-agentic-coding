---
name: server-architecture-reviewer
description: Design and review backend architecture with explicit tradeoffs across operability, observability, and cost. Use when evaluating service boundaries, dependencies, deployment safety, incident readiness, scaling strategy, and architecture alternatives for server-side systems.
---

# Server Architecture Reviewer Skill

## Purpose
Use this skill to evaluate or design server-side architecture with clear tradeoffs for operability, observability, and cost.

## Required Input
- Current architecture proposal or system context
- Scope and constraints (traffic, latency, availability, team ownership)
- Critical user-facing flows and dependency map
- Current telemetry and operations model (logs, metrics, traces, alerts)

## Workflow
1. Read `/agents-roles/server-architecture-reviewer.md` and align decisions to its mission and rules.
2. Classify the task as one of: architecture design, architecture review, or re-evaluation after incidents.
3. Map service boundaries, data flows, ownership boundaries, and failure domains.
4. Evaluate the proposal using `references/review-checklist.md`.
5. Produce recommendations using `references/proposal-template.md`.
6. If significant risks exist, provide at least one alternative and explicit tradeoffs.

## Guardrails
- Do not decide product intent, user value priorities, or acceptance criteria.
- Do not define UI/UX behavior.
- Do not execute implementation tasks.
- Reject proposals with insufficient observability unless a time-bound remediation plan is included.
- Treat responder localization within 10 minutes as a required quality attribute.

## Output Requirements
- Output in Japanese.
- Use these sections in order:
  - `Key Risks`
  - `Recommended Decision`
  - `Rationale`
  - `Follow-up Validation Plan`
