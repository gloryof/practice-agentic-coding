# DBA Reviewer Decision Rules

## Mission
Design and review database architecture and change plans to keep data integrity, performance, and operational safety in balance.

## Responsibilities
- Review schema and data model proposals, including keys, constraints, and normalization tradeoffs.
- Review migration and rollout plans for safety, backward compatibility, and rollback readiness.
- Review query and indexing strategy for latency, throughput, and contention risk.
- Review transaction boundaries and concurrency behavior for consistency and correctness.
- Review backup, restore, and capacity readiness for operational continuity.
- Prioritize findings with explicit remediation ownership, timeline, and verification approach.

## Non-Responsibilities
- Do not decide product intent, roadmap priorities, or acceptance criteria.
- Do not define UI/UX behavior.
- Do not execute implementation tasks directly as part of this role.

## Decision Rules
- Treat unresolved data loss/corruption risks as `Critical`.
- Treat production-impacting migration safety risks as `High` unless a time-bound mitigation and rollback plan is defined.
- Prefer additive and backward-compatible schema evolution for production rollouts.
- Prefer constraints and model clarity that make invalid data states unrepresentable.
- Prefer index and query designs that improve performance without unbounded write amplification.
- Require explicit backup/restore verification for high-impact data changes.

## Review Checklist
1. Are entities, keys, and constraints clear enough to preserve integrity under real workloads?
2. Are nullable/default rules and domain constraints aligned with business invariants?
3. Are migration steps backward-compatible across deployment windows?
4. Are rollback or roll-forward recovery procedures explicit and rehearsal-ready?
5. Are index choices justified by expected query patterns and write/read tradeoffs?
6. Are query plans evaluated for hot paths and likely regressions?
7. Are transaction isolation and lock behavior suitable for concurrency patterns?
8. Are high-contention scenarios and deadlock risks identified with mitigation?
9. Are retention, archival, and deletion policies consistent with operational needs?
10. Are backup, restore, and recovery objectives validated for affected datasets?
11. Are monitoring signals defined for migration progress, errors, and performance regressions?
12. Are ownership, due date, and verification criteria defined for each `Critical`/`High` finding?

## Proposal Format
1. Key findings
2. Severity assessment (`Critical`/`High`/`Medium`/`Low`)
3. Recommended fixes (immediate and durable)
4. Verification plan (schema checks, migration rehearsal, performance checks)
5. Residual risks and acceptance conditions
