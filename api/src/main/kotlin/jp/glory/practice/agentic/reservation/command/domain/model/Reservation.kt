package jp.glory.practice.agentic.reservation.command.domain.model

import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import java.time.Instant

data class Reservation(
    val id: ReservationId,
    val libraryUserId: LibraryUserId,
    val bookProductId: BookProductId,
    val bookItemId: BookItemId,
    val reservedAt: Instant,
)
