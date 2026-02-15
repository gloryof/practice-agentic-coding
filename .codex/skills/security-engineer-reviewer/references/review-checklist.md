# Security Review Checklist

## 1. Threat Modeling and Design
- Define protected assets, attacker capabilities, and trust boundaries.
- Validate data flow paths across services and external dependencies.
- Confirm threat scenarios for spoofing, tampering, data disclosure, and privilege escalation.
- Ensure failure modes degrade safely.

## 2. Authentication and Session Security
- Enforce strong authentication controls suitable for risk profile.
- Protect against brute force and credential stuffing with rate limits and lockout policy.
- Secure session lifecycle (creation, rotation, expiration, revocation).
- Prevent replay and fixation risks.

## 3. Authorization and Access Control
- Enforce server-side authorization for every protected operation.
- Validate object-level access (IDOR prevention).
- Apply least privilege for users, services, and tokens.
- Verify tenant/data partitioning in multi-tenant flows.

## 4. Input/Output Security
- Validate and normalize untrusted input at trust boundaries.
- Use parameterized queries and safe APIs to prevent injection.
- Encode output by context to mitigate XSS.
- Apply CSRF protections to state-changing endpoints.

## 5. Secrets and Cryptography
- Store secrets in dedicated secret managers; avoid source/log/client leakage.
- Use modern cryptographic algorithms and secure defaults.
- Protect keys with lifecycle management (generation, rotation, revocation).
- Enforce TLS and certificate hygiene.

## 6. Data Protection and Privacy
- Classify sensitive data and apply minimum-necessary retention.
- Encrypt sensitive data in transit and at rest.
- Mask/redact sensitive fields in logs and telemetry.
- Define secure deletion and backup protection controls.

## 7. Dependency and Supply Chain Security
- Pin and track dependency versions.
- Scan advisories and remediate known vulnerable packages.
- Validate build provenance and artifact integrity when possible.
- Limit third-party permissions and blast radius.

## 8. Error Handling, Logging, and Observability
- Avoid sensitive data in errors, traces, and logs.
- Ensure logs support incident investigation with correlation identifiers.
- Monitor abnormal auth/access/error patterns.
- Define actionable alerts for likely exploit behavior.

## 9. Verification and Release Gates
- Map tests to OWASP Top 10 and relevant OWASP ASVS controls.
- Include negative tests for authorization and input validation failures.
- Re-test fixed findings and add regression tests.
- Block release on unresolved `Critical`/`High` risks unless formally accepted.
