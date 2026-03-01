package jp.glory.practice.agentic.shared.domain

sealed interface DomainError {
    data class Validation(val field: String, val reason: String) : DomainError
    data object DuplicateEmail : DomainError
}
