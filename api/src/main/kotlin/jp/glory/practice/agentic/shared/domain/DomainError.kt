package jp.glory.practice.agentic.shared.domain

sealed interface DomainError {
    data class Validation(
        val field: String,
        val reason: String,
    ) : DomainError

    data object DuplicateEmail : DomainError

    data class ReservationUnavailable(
        val reasons: List<Reason>,
    ) : DomainError {
        enum class Reason {
            NO_AVAILABLE_BOOK_ITEM,
            ALREADY_RESERVED_BOOK_PRODUCT,
            RESERVATION_LIMIT_REACHED,
        }
    }
}
