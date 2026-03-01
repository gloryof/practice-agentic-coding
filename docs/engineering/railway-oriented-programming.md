# Railway Oriented Programming Policy

## Scope
- This policy applies to backend code in this repository.
- At this time, implementation code exists under `api/`.

## Core Rules
- Business failures must be represented with `Result`.
- Usecase public methods must return `Result<Success, UsecaseError>`.
- Domain and Usecase layers must not throw exceptions for business control.
- Web layer is the only layer that may throw `ApiException` for HTTP error mapping.
- Unexpected technical failures must be propagated as exceptions and converted to `500` by the global exception handler.

## Implementation Style
- Prefer composable chains (`zip`, `andThen`, `mapError`, `map`) for success/failure flow.
- Keep business error types centralized (`DomainError`, `UsecaseError`).
- Convert `UsecaseError` to `ApiException` through a shared mapper in Web layer.

## Local Verification
- Run `./gradlew check` in `api/`.
- Review throw usage with:
  - `rg -n "throw " api/src/main/kotlin`

## Allowed Exception Case
- `api/src/main/kotlin/jp/glory/practice/agentic/shared/spring/SecurityInfraConfig.kt` may throw a technical guard exception for unexpected encoder behavior.
