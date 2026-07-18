package jp.glory.practice.agentic.reservation.command.infra.adapter.persistence

import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.Reservation
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver
import jp.glory.practice.agentic.reservation.command.domain.repository.ReservationCommandRepository
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.ReservationDao
import org.springframework.stereotype.Repository

@Repository
class ReservationCommandRepositoryImpl(
    private val reservationDao: ReservationDao,
) : ReservationCommandRepository {
    override fun lockReserver(libraryUserId: LibraryUserId): Boolean {
        val userId = libraryUserId.value
        return reservationDao.lockLibraryUser(userId)
    }

    override fun findReserver(libraryUserId: LibraryUserId): Reserver =
        Reserver(
            libraryUserId = libraryUserId,
            reservedBookProductIds =
                reservationDao
                    .findReservedBookProductIds(libraryUserId.value)
                    .map(::BookProductId)
                    .toSet(),
        )

    override fun findTarget(bookProductId: BookProductId): ReservationTargetBookProduct? {
        val row = reservationDao.findBookProduct(bookProductId.value) ?: return null
        return ReservationTargetBookProduct(
            bookProductId = BookProductId(row.id),
            title = row.title,
            isbn = row.isbn,
            availableBookItemCount = reservationDao.countAvailableBookItems(bookProductId.value),
        )
    }

    override fun reserveAvailableBookItem(bookProductId: BookProductId): BookItemId? {
        val productId = bookProductId.value
        return reservationDao.reserveAvailableBookItem(productId)?.let(::BookItemId)
    }

    override fun save(reservation: Reservation) {
        reservationDao.insertReservation(
            id = reservation.id.value,
            libraryUserId = reservation.libraryUserId.value,
            bookProductId = reservation.bookProductId.value,
            bookItemId = reservation.bookItemId.value,
            reservedAt = reservation.reservedAt,
        )
    }
}
