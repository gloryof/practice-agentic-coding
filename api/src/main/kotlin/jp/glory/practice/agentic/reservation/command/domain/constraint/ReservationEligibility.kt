package jp.glory.practice.agentic.reservation.command.domain.constraint

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver
import jp.glory.practice.agentic.shared.domain.DomainError

class ReservationEligibility {
    fun evaluate(
        reserver: Reserver,
        target: ReservationTargetBookProduct,
    ): Result<Unit, DomainError> {
        val reasons = mutableListOf<DomainError.ReservationUnavailable.Reason>()
        if (!target.hasAvailableBookItem()) {
            reasons.add(DomainError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM)
        }
        if (reserver.hasReserved(target.bookProductId)) {
            reasons.add(DomainError.ReservationUnavailable.Reason.ALREADY_RESERVED_BOOK_PRODUCT)
        }
        if (!reserver.canReserveMore()) {
            reasons.add(DomainError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED)
        }
        return if (reasons.isEmpty()) {
            Ok(Unit)
        } else {
            Err(DomainError.ReservationUnavailable(reasons))
        }
    }
}
