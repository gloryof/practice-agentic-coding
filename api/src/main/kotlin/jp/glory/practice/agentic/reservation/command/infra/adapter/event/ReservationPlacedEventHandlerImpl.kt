package jp.glory.practice.agentic.reservation.command.infra.adapter.event

import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEvent
import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEventHandler
import jp.glory.practice.agentic.reservation.command.domain.repository.ReservationCommandRepository
import org.springframework.stereotype.Component

@Component
class ReservationPlacedEventHandlerImpl(
    private val reservationRepository: ReservationCommandRepository,
) : ReservationPlacedEventHandler {
    override fun handle(event: ReservationPlacedEvent) {
        reservationRepository.save(event.toReservation())
    }
}
