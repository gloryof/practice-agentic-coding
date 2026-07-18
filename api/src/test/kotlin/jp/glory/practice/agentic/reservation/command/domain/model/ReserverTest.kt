package jp.glory.practice.agentic.reservation.command.domain.model

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReserverTest {
    @Nested
    inner class CanReserveMore {
        @Test
        fun `given two active reservations when can reserve more then returns true`() {
            val sut =
                reserver(
                    reservedBookProductIds =
                        setOf(
                            BookProductId("book-1"),
                            BookProductId("book-2"),
                        ),
                )

            assertTrue(sut.canReserveMore())
        }

        @Test
        fun `given three active reservations when can reserve more then returns false`() {
            val sut =
                reserver(
                    reservedBookProductIds =
                        setOf(
                            BookProductId("book-1"),
                            BookProductId("book-2"),
                            BookProductId("book-3"),
                        ),
                )

            assertFalse(sut.canReserveMore())
        }
    }

    @Nested
    inner class HasReserved {
        @Test
        fun `given reserved book product id when has reserved then returns true`() {
            val sut = reserver(reservedBookProductIds = setOf(BookProductId("book-1")))

            assertTrue(sut.hasReserved(BookProductId("book-1")))
        }

        @Test
        fun `given not reserved book product id when has reserved then returns false`() {
            val sut = reserver(reservedBookProductIds = setOf(BookProductId("book-1")))

            assertFalse(sut.hasReserved(BookProductId("book-2")))
        }
    }

    private fun reserver(reservedBookProductIds: Set<BookProductId>): Reserver =
        Reserver(
            libraryUserId = LibraryUserId("user-1"),
            reservedBookProductIds = reservedBookProductIds,
        )
}
