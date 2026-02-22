# AGENTS.md

## Project Overview
- This project builds a community library discovery service.
- See /product/product-foundation.md for detailed product goals.

## Agents Priority
- When working inside a subdirectory, prioritize and follow that subdirectory's `AGENTS.md` over the root `AGENTS.md`.
- Apply the root `AGENTS.md` as default guidance only when no closer `AGENTS.md` exists.

## PO Skills Routing
- Use `po-story` when the task is to create a user story.
- Use `po-spec` when the task is to evaluate, create, or update a specification.
- Use `po-spec` for specification Q&A about product intent, requirements, acceptance criteria, and constraints.
- For implementation-level questions, keep PO scope boundary clear and escalate to engineering roles.
- Use `server-architecture-reviewer` for backend architecture design and reviews, including cost, operability, and observability tradeoffs.
- Use `security-engineer-reviewer` for security-focused design and coding reviews, including threat surfaces, access control, data protection, and remediation priorities.
- Use `qa-test-reviewer` for QA-focused test code reviews, especially unit-test reliability, maintainability, flaky-risk analysis, and CI feedback quality.

## Skills
A skill is a set of local instructions to follow that is stored in a `SKILL.md` file. Below is the list of skills that can be used. Each entry includes a name, description, and file path so you can open the source for full instructions when using a specific skill.
### Available skills
- po-spec: Evaluate, create, and update specifications based on user value, and answer specification questions within PO scope. (file: `.codex/skills/po-spec/SKILL.md`)
- po-story: Create user stories with clear user value and testable acceptance criteria for the community library discovery service. (file: `.codex/skills/po-story/SKILL.md`)
- server-architecture-reviewer: Design and review backend architecture with explicit tradeoffs across operability, observability, and cost. Use when evaluating service boundaries, dependencies, deployment safety, incident readiness, scaling strategy, and architecture alternatives for server-side systems. (file: `.codex/skills/server-architecture-reviewer/SKILL.md`)
- security-engineer-reviewer: Review security in both system design and implementation with explicit risk tradeoffs and actionable remediation. Use when evaluating threat surfaces, trust boundaries, authentication/authorization, data protection, dependency risk, and secure coding issues for backend or full-stack systems. (file: `.codex/skills/security-engineer-reviewer/SKILL.md`)
- qa-test-reviewer: Review test code quality from a QA perspective, focused on unit-test reliability, maintainability, and execution efficiency. Use when evaluating test correctness, flaky risk, assertion quality, fixture/mocking strategy, and CI stability impact. (file: `.codex/skills/qa-test-reviewer/SKILL.md`)

## Documentation Path Policy
- AI-generated and human-authored documents `MUST NOT` include machine-local absolute paths (for example: `/Users/...`, `/home/...`, `C:\Users\...`, `file:///...`).
- `MUST` use repository-relative paths for files in this repository (for example: `.codex/skills/po-spec/SKILL.md`).
- `MUST` use environment variables for external paths only when unavoidable (for example: `$CODEX_HOME/skills/...`), and explain why.
- `MUST` run `./scripts/check-no-local-paths.sh` before finalizing documentation changes.
