---
name: po-spec
description: Evaluate, create, and update specifications based on user value, and answer specification questions within PO scope.
---

# PO Spec Skill

## Purpose
Use this skill for specification decisions and updates, and for answering specification questions about product intent.

## Required Input
- Current specification or draft
- Request for change or clarification
- Product constraints and user context

## Workflow
1. Read `/agents-roles/po.md` before producing output.
2. Classify input as one of: new spec, spec update, specification question.
3. Evaluate with PO decision rules and prioritize user benefit.
4. Produce recommendation or updated specification.
5. If the question is implementation-level, mark it out of scope and escalate.

## Output Requirements
- `User Benefit`: one short paragraph.
- `Decision`: one of "Adopt", "Revise", "Reject", or "Answer".
- `Specification`: new or updated specification text.
- `Rationale`: brief mapping to PO decision rules.
- `Scope Boundary`: what this answer covers and what it does not cover.
- `Escalation`: required only when implementation-level follow-up is needed.
