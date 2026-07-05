package jp.glory.practice.agentic.reservation.command.domain.model

enum class ReservationUnavailableReason(
    val code: String,
) {
    NO_AVAILABLE_BOOK_ITEM("no_available_book_item"),
    ALREADY_RESERVED_BOOK_PRODUCT("already_reserved_book_product"),
    RESERVATION_LIMIT_REACHED("reservation_limit_reached"),
    RESERVATION_CONFLICT("reservation_conflict"),
}
