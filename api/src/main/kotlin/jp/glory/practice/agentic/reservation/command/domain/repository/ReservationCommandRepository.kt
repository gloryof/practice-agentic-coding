package jp.glory.practice.agentic.reservation.command.domain.repository

import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.Reservation
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver

interface ReservationCommandRepository {
    fun lockReserver(libraryUserId: LibraryUserId): Boolean

    fun findReserver(libraryUserId: LibraryUserId): Reserver

    fun findTarget(bookProductId: BookProductId): ReservationTargetBookProduct?

    fun reserveAvailableBookItem(bookProductId: BookProductId): BookItemId?

    fun save(reservation: Reservation)
}
