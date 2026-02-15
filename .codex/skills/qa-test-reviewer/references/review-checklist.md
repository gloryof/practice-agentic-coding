# QA Test Code Review Checklist

## 1. Test Correctness
- Confirm test cases map to intended behavior and risk areas.
- Validate positive, negative, and boundary scenarios.
- Check that assertions verify behavior, not incidental implementation details.
- Ensure failure conditions are meaningful and diagnosable.

## 2. Determinism and Flaky Risk
- Detect time-dependent assumptions (clock, timezone, sleep-based waits).
- Detect order dependence and shared mutable state.
- Detect random/non-seeded behavior and race-prone concurrency checks.
- Detect external dependency coupling (network, filesystem, environment state).

## 3. Test Isolation and Data Setup
- Ensure each test can run independently and repeatably.
- Evaluate fixture scope and lifecycle for leakage/crosstalk.
- Ensure mock/stub/fake usage reflects realistic contracts.
- Minimize setup noise while keeping behavior clear.

## 4. Maintainability and Readability
- Verify naming clarity for suites, cases, and helper utilities.
- Identify duplicated test patterns suitable for refactoring.
- Keep helper abstractions simple and behavior-oriented.
- Ensure test intent is quickly understandable by reviewers.

## 5. Execution Efficiency and CI Stability
- Identify slow tests and unnecessary heavyweight setup.
- Confirm unit tests are optimized for fast feedback.
- Evaluate parallel-execution safety and shared-resource contention.
- Confirm rerun behavior and diagnostics support rapid triage.

## 6. Regression Prevention
- Verify tests exist for previously fixed defects.
- Ensure missing high-risk regression scenarios are explicitly called out.
- Confirm improvements include measurable pass/fail validation criteria.
- Track residual flaky risk with owner and follow-up window.
