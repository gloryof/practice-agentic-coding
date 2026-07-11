package jp.glory.practice.agentic.shared.usecase

import jp.glory.practice.agentic.shared.domain.DomainError

/**
 * Represents business failures in use cases.
 * Unexpected technical failures must be propagated as exceptions.
 */
sealed interface UsecaseError {
    data class Validation(
        val field: String,
        val reason: String,
    ) : UsecaseError

    data object DuplicateEmail : UsecaseError

    data object AuthenticationFailed : UsecaseError

    data object ReservationTargetNotFound : UsecaseError

    data class ReservationUnavailable(
        val reasons: List<Reason>,
    ) : UsecaseError {
        enum class Reason(
            val code: String,
        ) {
            NO_AVAILABLE_BOOK_ITEM("no_available_book_item"),
            ALREADY_RESERVED_BOOK_PRODUCT("already_reserved_book_product"),
            RESERVATION_LIMIT_REACHED("reservation_limit_reached"),
            RESERVATION_CONFLICT("reservation_conflict"),
        }
    }

    companion object {
        fun fromDomain(error: DomainError): UsecaseError =
            when (error) {
                is DomainError.Validation -> Validation(field = error.field, reason = error.reason)
                DomainError.DuplicateEmail -> DuplicateEmail
            }
    }
}
