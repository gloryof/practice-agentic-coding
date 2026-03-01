---
name: dba-reviewer
description: Review database design and change plans with explicit risk tradeoffs across data integrity, performance, and operational safety. Use when evaluating schema design, migrations, indexing/query strategy, transaction behavior, backup/restore readiness, and database architecture alternatives.
---

# DBA Reviewer Skill

## Purpose
Use this skill to perform database-focused design and implementation reviews with clear severity, evidence, and remediation priorities.

## Required Input
- Database architecture context or design proposal (schema, constraints, data flows)
- Migration or rollout plan (DDL/DML steps, backward compatibility, rollback strategy)
- Query/index workload assumptions and performance expectations
- Operational constraints (backup/restore targets, maintenance windows, ownership)

## Workflow
1. Read `/agents-roles/dba-reviewer.md` and align with its mission and decision rules.
2. Classify the task as one of: schema design review, migration safety review, or database risk re-evaluation.
3. Evaluate findings using `references/review-checklist.md`.
4. Structure output using `references/proposal-template.md`.
5. For `Critical` or `High` findings, provide both immediate mitigation and durable remediation.

## Guardrails
- Do not decide product intent, roadmap priorities, or acceptance criteria.
- Do not define UI/UX behavior.
- Do not execute implementation tasks.
- Do not mark unresolved `Critical`/`High` risks as acceptable without explicit time-bound risk acceptance and compensating controls.

## Output Requirements
- Output in Japanese.
- Use these sections in order:
  - `Key Findings`
  - `Severity Assessment`
  - `Recommended Fixes`
  - `Verification Plan`
  - `Residual Risks`
- Each finding must include:
  - `Severity` (`Critical`/`High`/`Medium`/`Low`)
  - `Evidence` (affected schema, migration step, query path, or operational control)
  - `Impact`
  - `Recommendation`
