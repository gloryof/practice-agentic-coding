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
- Relevant user stories and domain specifications discovered under `product/domain-context`

## Workflow
1. Read `agents/roles/po.md` before producing output.
2. Read `product/domain-context/README.md`, then search `product/domain-context` by context name, file name, and specification terms to identify the relevant domain documents.
3. Read `product/product-foundation.md`, the relevant user stories, and the domain documents identified by the search.
4. Classify input as one of: new spec, spec update, specification question.
5. Evaluate with PO decision rules and prioritize user benefit.
6. Update `product/domain-context` for user value, use-case behavior, and domain details.
7. Update the relevant user stories when the user story or acceptance criteria change.
8. Place new domain documents according to the structure, naming, and link rules in `product/domain-context/README.md`.
9. Produce recommendation or updated specification.
10. If the question is implementation-level, mark it out of scope and escalate.

## Output Requirements
- `User Benefit`: one short paragraph.
- `Decision`: one of "Adopt", "Revise", "Reject", or "Answer".
- `Specification`: new or updated specification text.
- `Rationale`: brief mapping to PO decision rules.
- `Scope Boundary`: what this answer covers and what it does not cover.
- `Escalation`: required only when implementation-level follow-up is needed.
