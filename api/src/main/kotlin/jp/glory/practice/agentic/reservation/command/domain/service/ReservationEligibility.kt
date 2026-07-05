package jp.glory.practice.agentic.reservation.command.domain.service

import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationUnavailableReason
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver

class ReservationEligibility {
    fun evaluate(
        reserver: Reserver,
        target: ReservationTargetBookProduct,
    ): ReservationEligibilityResult {
        val reasons = mutableListOf<ReservationUnavailableReason>()
        if (!target.hasAvailableBookItem()) {
            reasons.add(ReservationUnavailableReason.NO_AVAILABLE_BOOK_ITEM)
        }
        if (reserver.hasReserved(target.bookProductId)) {
            reasons.add(ReservationUnavailableReason.ALREADY_RESERVED_BOOK_PRODUCT)
        }
        if (!reserver.canReserveMore()) {
            reasons.add(ReservationUnavailableReason.RESERVATION_LIMIT_REACHED)
        }
        return ReservationEligibilityResult(reasons)
    }
}

data class ReservationEligibilityResult(
    val reasons: List<ReservationUnavailableReason>,
) {
    fun isEligible(): Boolean = reasons.isEmpty()
}
