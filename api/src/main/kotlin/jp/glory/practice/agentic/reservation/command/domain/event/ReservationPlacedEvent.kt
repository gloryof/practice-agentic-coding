package jp.glory.practice.agentic.reservation.command.domain.event

import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.Reservation
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationId
import java.time.Instant

data class ReservationPlacedEvent(
    val reservationId: ReservationId,
    val libraryUserId: LibraryUserId,
    val bookProductId: BookProductId,
    val bookItemId: BookItemId,
    val occurredAt: Instant,
) {
    fun toReservation(): Reservation =
        Reservation(
            id = reservationId,
            libraryUserId = libraryUserId,
            bookProductId = bookProductId,
            bookItemId = bookItemId,
            reservedAt = occurredAt,
        )
}

fun interface ReservationPlacedEventHandler {
    fun handle(event: ReservationPlacedEvent)
}
