package jp.glory.practice.agentic.reservation.command.domain.model

data class Reserver(
    val libraryUserId: LibraryUserId,
    val reservedBookProductIds: Set<BookProductId>,
) {
    fun canReserveMore(): Boolean = reservedBookProductIds.size < MAX_ACTIVE_RESERVATIONS

    fun hasReserved(bookProductId: BookProductId): Boolean = reservedBookProductIds.contains(bookProductId)

    private companion object {
        const val MAX_ACTIVE_RESERVATIONS = 3
    }
}
