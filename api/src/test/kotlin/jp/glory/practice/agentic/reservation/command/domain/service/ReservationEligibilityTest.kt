package jp.glory.practice.agentic.reservation.command.domain.service

import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationUnavailableReason
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReservationEligibilityTest {
    @Nested
    inner class Evaluate {
        @Test
        fun `given eligible reserver and target when evaluate then returns no reasons`() {
            val sut = ReservationEligibility()

            val result =
                sut.evaluate(
                    reserver = reserver(reservedBookProductIds = setOf(BookProductId("book-1"))),
                    target = target(bookProductId = BookProductId("book-2"), availableBookItemCount = 1),
                )

            assertTrue(result.isEligible())
            assertEquals(emptyList(), result.reasons)
        }

        @Test
        fun `given no available book item only when evaluate then returns no available book item reason`() {
            val sut = ReservationEligibility()

            val result =
                sut.evaluate(
                    reserver = reserver(reservedBookProductIds = setOf(BookProductId("book-1"))),
                    target = target(bookProductId = BookProductId("book-2"), availableBookItemCount = 0),
                )

            assertEquals(
                listOf(ReservationUnavailableReason.NO_AVAILABLE_BOOK_ITEM),
                result.reasons,
            )
        }

        @Test
        fun `given already reserved book product only when evaluate then returns already reserved reason`() {
            val sut = ReservationEligibility()
            val bookProductId = BookProductId("book-1")

            val result =
                sut.evaluate(
                    reserver = reserver(reservedBookProductIds = setOf(bookProductId)),
                    target = target(bookProductId = bookProductId, availableBookItemCount = 1),
                )

            assertEquals(
                listOf(ReservationUnavailableReason.ALREADY_RESERVED_BOOK_PRODUCT),
                result.reasons,
            )
        }

        @Test
        fun `given reservation limit reached only when evaluate then returns reservation limit reason`() {
            val sut = ReservationEligibility()

            val result =
                sut.evaluate(
                    reserver =
                        reserver(
                            reservedBookProductIds =
                                setOf(
                                    BookProductId("book-1"),
                                    BookProductId("book-2"),
                                    BookProductId("book-3"),
                                ),
                        ),
                    target = target(bookProductId = BookProductId("book-4"), availableBookItemCount = 1),
                )

            assertEquals(
                listOf(ReservationUnavailableReason.RESERVATION_LIMIT_REACHED),
                result.reasons,
            )
        }

        @Test
        fun `given all conditions violated when evaluate then returns all reasons`() {
            val sut = ReservationEligibility()
            val bookProductId = BookProductId("book-1")

            val result =
                sut.evaluate(
                    reserver =
                        reserver(
                            reservedBookProductIds =
                                setOf(
                                    bookProductId,
                                    BookProductId("book-2"),
                                    BookProductId("book-3"),
                                ),
                        ),
                    target = target(bookProductId = bookProductId, availableBookItemCount = 0),
                )

            assertEquals(
                listOf(
                    ReservationUnavailableReason.NO_AVAILABLE_BOOK_ITEM,
                    ReservationUnavailableReason.ALREADY_RESERVED_BOOK_PRODUCT,
                    ReservationUnavailableReason.RESERVATION_LIMIT_REACHED,
                ),
                result.reasons,
            )
        }
    }

    private fun reserver(reservedBookProductIds: Set<BookProductId>): Reserver =
        Reserver(
            libraryUserId = LibraryUserId("user-1"),
            reservedBookProductIds = reservedBookProductIds,
        )

    private fun target(
        bookProductId: BookProductId,
        availableBookItemCount: Int,
    ): ReservationTargetBookProduct =
        ReservationTargetBookProduct(
            bookProductId = bookProductId,
            title = "Kotlin入門",
            isbn = "9780000000001",
            availableBookItemCount = availableBookItemCount,
        )
}
