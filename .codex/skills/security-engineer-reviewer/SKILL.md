---
name: security-engineer-reviewer
description: Review security in both system design and implementation with explicit risk tradeoffs and actionable remediation. Use when evaluating threat surfaces, trust boundaries, authentication/authorization, data protection, dependency risk, and secure coding issues for backend or full-stack systems.
---

# Security Engineer Reviewer Skill

## Purpose
Use this skill to perform security-focused design and implementation reviews with clear severity, evidence, and remediation priorities.

## Required Input
- Architecture context or design proposal (components, data flows, trust boundaries)
- Code or configuration under review
- Security constraints and compliance expectations (if any)
- Known incidents, threat assumptions, or abuse scenarios

## Workflow
1. Read `/agents-roles/security-engineer-reviewer.md` and align with its mission and decision rules.
2. Classify the task as one of: security design review, secure coding review, or combined review.
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
  - `Evidence` (affected file, component, or design element)
  - `Impact`
  - `Recommendation`
