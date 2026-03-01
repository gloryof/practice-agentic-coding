# DBA Review Checklist

## 1. Data Modeling and Integrity
- Confirm entities, keys, and relationships match domain invariants.
- Validate PK/FK/UNIQUE/CHECK constraints for correctness and completeness.
- Confirm nullable/default rules prevent invalid business states.
- Evaluate normalization/denormalization tradeoffs with explicit rationale.

## 2. Migration Safety and Change Management
- Verify additive, backward-compatible rollout strategy where feasible.
- Confirm zero/low-downtime migration sequencing across app versions.
- Validate rollback or roll-forward recovery plan with clear triggers.
- Require migration rehearsal steps and success/failure criteria.

## 3. Query and Index Performance
- Validate critical query paths and expected access patterns.
- Confirm index strategy balances read latency and write amplification.
- Check execution plan quality for hot-path queries.
- Identify regression risks such as full scans, sort spills, and N+1 patterns.

## 4. Transaction and Concurrency Control
- Confirm transaction boundaries align with consistency requirements.
- Validate isolation level choices against anomaly risks.
- Evaluate lock scope/duration and potential contention hotspots.
- Identify deadlock-prone flows and mitigation strategy.

## 5. Operations, Capacity, and Reliability
- Confirm backup/restore strategy and test cadence for affected datasets.
- Validate retention, archival, and deletion policies.
- Evaluate storage growth, vacuum/maintenance needs, and capacity headroom.
- Confirm runbook readiness for migration incidents and degraded performance.

## 6. Observability and Verification
- Ensure migration progress and failure metrics are defined.
- Ensure query latency/error/lock metrics are observable for impacted paths.
- Confirm alerts are actionable and mapped to likely DB failure modes.
- Confirm release gates include schema validation, migration rehearsal, and post-release checks.

## 7. Security and Data Protection
- Confirm sensitive columns are protected according to policy (encryption/masking/access).
- Ensure least-privilege DB roles and credentials separation.
- Validate auditability of high-risk data and schema changes.
- Confirm no secrets or sensitive data leak through logs and error outputs.
