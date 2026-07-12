package jp.glory.practice.agentic.reservation.command.domain.constraint

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver
import jp.glory.practice.agentic.shared.domain.DomainError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

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

            assertEquals(Ok(Unit), result)
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
                Err(
                    DomainError.ReservationUnavailable(
                        listOf(DomainError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM),
                    ),
                ),
                result,
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
                Err(
                    DomainError.ReservationUnavailable(
                        listOf(DomainError.ReservationUnavailable.Reason.ALREADY_RESERVED_BOOK_PRODUCT),
                    ),
                ),
                result,
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
                Err(
                    DomainError.ReservationUnavailable(
                        listOf(DomainError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED),
                    ),
                ),
                result,
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
                Err(
                    DomainError.ReservationUnavailable(
                        listOf(
                            DomainError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM,
                            DomainError.ReservationUnavailable.Reason.ALREADY_RESERVED_BOOK_PRODUCT,
                            DomainError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED,
                        ),
                    ),
                ),
                result,
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
