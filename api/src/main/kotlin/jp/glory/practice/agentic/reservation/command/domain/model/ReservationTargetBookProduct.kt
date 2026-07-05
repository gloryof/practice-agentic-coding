package jp.glory.practice.agentic.reservation.command.domain.model

data class ReservationTargetBookProduct(
    val bookProductId: BookProductId,
    val title: String,
    val isbn: String,
    val availableBookItemCount: Int,
) {
    fun hasAvailableBookItem(): Boolean = availableBookItemCount > 0
}
