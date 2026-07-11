package jp.glory.practice.agentic.reservation.command.domain.constraint

import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver

class ReservationEligibility {
    fun evaluate(
        reserver: Reserver,
        target: ReservationTargetBookProduct,
    ): ReservationEligibilityResult {
        val violations = mutableListOf<ReservationEligibilityViolation>()
        if (!target.hasAvailableBookItem()) {
            violations.add(ReservationEligibilityViolation.NO_AVAILABLE_BOOK_ITEM)
        }
        if (reserver.hasReserved(target.bookProductId)) {
            violations.add(ReservationEligibilityViolation.ALREADY_RESERVED_BOOK_PRODUCT)
        }
        if (!reserver.canReserveMore()) {
            violations.add(ReservationEligibilityViolation.RESERVATION_LIMIT_REACHED)
        }
        return ReservationEligibilityResult(violations)
    }
}

data class ReservationEligibilityResult(
    val violations: List<ReservationEligibilityViolation>,
) {
    fun isSatisfied(): Boolean = violations.isEmpty()
}

enum class ReservationEligibilityViolation {
    NO_AVAILABLE_BOOK_ITEM,
    ALREADY_RESERVED_BOOK_PRODUCT,
    RESERVATION_LIMIT_REACHED,
}
