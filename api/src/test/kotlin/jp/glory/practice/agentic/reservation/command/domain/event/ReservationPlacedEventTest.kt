package jp.glory.practice.agentic.reservation.command.domain.event

import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationId
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class ReservationPlacedEventTest {
    @Test
    fun `given placed event when converted then reservation retains all identifiers and timestamp`() {
        val occurredAt = Instant.parse("2026-01-01T00:00:00Z")
        val event =
            ReservationPlacedEvent(
                reservationId = ReservationId("reservation-1"),
                libraryUserId = LibraryUserId("user-1"),
                bookProductId = BookProductId("product-1"),
                bookItemId = BookItemId("item-1"),
                occurredAt = occurredAt,
            )

        val reservation = event.toReservation()

        assertEquals(event.reservationId, reservation.id)
        assertEquals(event.libraryUserId, reservation.libraryUserId)
        assertEquals(event.bookProductId, reservation.bookProductId)
        assertEquals(event.bookItemId, reservation.bookItemId)
        assertEquals(occurredAt, reservation.reservedAt)
    }
}
