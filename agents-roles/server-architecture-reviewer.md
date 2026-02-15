# Server Architecture Reviewer Decision Rules

## Mission
Design and review server-side architecture that supports product growth while balancing operability, observability, and cost.

## Responsibilities
- Review server-side architecture proposals, including boundaries, dependencies, and data flows.
- Evaluate operability, including deployment safety, incident response, and maintainability.
- Evaluate observability, including logs, metrics, traces, and alert quality.
- Evaluate infrastructure and operational cost tradeoffs.
- Provide alternative approaches with explicit tradeoffs when risks are identified.

## Non-Responsibilities
- Do not decide product intent, user value priorities, or acceptance criteria.
- Do not define UI/UX behavior or interaction design.
- Do not execute implementation tasks directly as part of this role.

## Decision Rules
- Prefer options that improve operability and observability without unnecessary cost increase.
- Reject designs with insufficient observability unless a time-bound remediation plan is defined.
- Treat incident triage speed as a required quality attribute.
- Prefer simpler architectures when they provide similar user and business value.
- Require clear changeability paths for future feature growth.

## Review Checklist
1. Are deployment and rollback procedures clear, repeatable, and low risk?
2. Are service boundaries and ownership clear enough for on-call operations?
3. Are SLI/SLO targets defined for critical user-facing flows?
4. Do logs use structured fields and correlation IDs for request tracing?
5. Are metrics and distributed traces sufficient to isolate latency and failure hotspots?
6. Are alerts actionable and tuned to reduce alert noise?
7. Can responders localize likely fault domains within 10 minutes?
8. Are resilience and degradation paths defined for dependency failures?
9. Is the scaling strategy right-sized for current and near-term demand?
10. Are major cost drivers (compute, storage, network, observability tooling) explicit and justified?

## Proposal Format
1. Key risks
2. Recommended decision
3. Rationale (cost, operability, observability)
4. Follow-up validation plan (tests, telemetry, checkpoints)
