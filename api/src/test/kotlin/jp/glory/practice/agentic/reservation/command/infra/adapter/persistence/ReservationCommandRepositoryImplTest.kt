package jp.glory.practice.agentic.reservation.command.infra.adapter.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.Reservation
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.ReservationBookProductRow
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.ReservationDao
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReservationCommandRepositoryImplTest {
    @Nested
    inner class LockReserver {
        @Test
        fun `given existing library user when lock reserver then returns true`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.lockLibraryUser("user-1") } returns true

            val result = sut.lockReserver(LibraryUserId("user-1"))

            assertTrue(result)
            verify(exactly = 1) { dao.lockLibraryUser("user-1") }
        }

        @Test
        fun `given missing library user when lock reserver then returns false`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.lockLibraryUser("user-2") } returns false

            val result = sut.lockReserver(LibraryUserId("user-2"))

            assertFalse(result)
        }
    }

    @Nested
    inner class FindReserver {
        @Test
        fun `given multiple reservations when find reserver then converts book product ids`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            val libraryUserId = LibraryUserId("user-3")
            every { dao.findReservedBookProductIds("user-3") } returns listOf("product-1", "product-2")

            val result = sut.findReserver(libraryUserId)

            assertEquals(libraryUserId, result.libraryUserId)
            assertEquals(setOf(BookProductId("product-1"), BookProductId("product-2")), result.reservedBookProductIds)
        }

        @Test
        fun `given no reservations when find reserver then returns empty reserved book products`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.findReservedBookProductIds("user-4") } returns emptyList()

            val result = sut.findReserver(LibraryUserId("user-4"))

            assertTrue(result.reservedBookProductIds.isEmpty())
        }
    }

    @Nested
    inner class FindTarget {
        @Test
        fun `given existing book product when find target then converts dao results`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.findBookProduct("product-3") } returns
                ReservationBookProductRow(
                    id = "product-3",
                    title = "Domain-Driven Design",
                    isbn = "9780321125217",
                )
            every { dao.countAvailableBookItems("product-3") } returns 2

            val result = sut.findTarget(BookProductId("product-3"))

            assertNotNull(result)
            assertEquals(BookProductId("product-3"), result.bookProductId)
            assertEquals("Domain-Driven Design", result.title)
            assertEquals("9780321125217", result.isbn)
            assertEquals(2, result.availableBookItemCount)
        }

        @Test
        fun `given missing book product when find target then returns null`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.findBookProduct("product-4") } returns null

            val result = sut.findTarget(BookProductId("product-4"))

            assertNull(result)
            verify(exactly = 0) { dao.countAvailableBookItems(any()) }
        }
    }

    @Nested
    inner class ReserveAvailableBookItem {
        @Test
        fun `given available book item when reserve then converts reserved book item id`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.reserveAvailableBookItem("product-5") } returns "item-1"

            val result = sut.reserveAvailableBookItem(BookProductId("product-5"))

            assertEquals(BookItemId("item-1"), result)
        }

        @Test
        fun `given reservation conflict when reserve then returns null`() {
            val dao = mockk<ReservationDao>()
            val sut = ReservationCommandRepositoryImpl(dao)
            every { dao.reserveAvailableBookItem("product-6") } returns null

            val result = sut.reserveAvailableBookItem(BookProductId("product-6"))

            assertNull(result)
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `given reservation when save then delegates all fields to dao`() {
            val dao = mockk<ReservationDao>(relaxed = true)
            val sut = ReservationCommandRepositoryImpl(dao)
            val reservation =
                Reservation(
                    id = ReservationId("reservation-1"),
                    libraryUserId = LibraryUserId("user-5"),
                    bookProductId = BookProductId("product-7"),
                    bookItemId = BookItemId("item-2"),
                    reservedAt = Instant.parse("2026-07-12T01:23:45Z"),
                )

            sut.save(reservation)

            verify(exactly = 1) {
                dao.insertReservation(
                    id = "reservation-1",
                    libraryUserId = "user-5",
                    bookProductId = "product-7",
                    bookItemId = "item-2",
                    reservedAt = Instant.parse("2026-07-12T01:23:45Z"),
                )
            }
        }
    }
}
