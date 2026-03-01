package jp.glory.practice.agentic.shared.usecase

import jp.glory.practice.agentic.shared.domain.DomainError

/**
 * Represents business failures in use cases.
 * Unexpected technical failures must be propagated as exceptions.
 */
sealed interface UsecaseError {
    data class Validation(val field: String, val reason: String) : UsecaseError
    data object DuplicateEmail : UsecaseError
    data object AuthenticationFailed : UsecaseError

    companion object {
        fun fromDomain(error: DomainError): UsecaseError {
            return when (error) {
                is DomainError.Validation -> Validation(field = error.field, reason = error.reason)
                DomainError.DuplicateEmail -> DuplicateEmail
            }
        }
    }
}
