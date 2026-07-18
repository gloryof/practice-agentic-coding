package jp.glory.practice.agentic.reservation.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.getOrThrow
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.reservation.command.domain.constraint.ReservationEligibility
import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEvent
import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEventHandler
import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.model.Reserver
import jp.glory.practice.agentic.reservation.command.domain.repository.ReservationCommandRepository
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.junit.jupiter.api.Nested
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceReservationUseCaseTest {
    private data class TestContext(
        val sut: PlaceReservationUseCase,
        val repository: ReservationCommandRepository,
        val eventHandler: ReservationPlacedEventHandler,
    )

    @Nested
    inner class Place {
        @Test
        fun `given eligible reservation when place then reserves book item and handles event`() {
            val context = createSut()
            val eventSlot = slot<ReservationPlacedEvent>()
            every { context.repository.findTarget(BookProductId("book-1")) } returns target()
            every { context.repository.lockReserver(LibraryUserId("user-1")) } returns true
            every { context.repository.findReserver(LibraryUserId("user-1")) } returns reserver()
            every { context.repository.reserveAvailableBookItem(BookProductId("book-1")) } returns BookItemId("item-1")
            every { context.eventHandler.handle(any()) } just Runs

            val result =
                context.sut
                    .place(PlaceReservationInput(libraryUserId = "user-1", bookProductId = "book-1"))
                    .getOrThrow { error("expected success") }

            assertEquals("book-1", result.bookProductId)
            assertEquals("Kotlin入門", result.title)
            assertEquals("9780000000001", result.isbn)
            assertEquals("item-1", result.bookItemId)
            assertEquals(Instant.parse("2026-02-22T12:34:56Z"), result.reservedAt)
            assertEquals("ReservationPlacedEvent", result.eventName)
            verify(exactly = 1) { context.eventHandler.handle(capture(eventSlot)) }
            assertReservationPlacedEvent(
                event = eventSlot.captured,
                reservationId = result.reservationId,
                libraryUserId = "user-1",
                bookProductId = "book-1",
                bookItemId = "item-1",
                occurredAt = Instant.parse("2026-02-22T12:34:56Z"),
            )
        }

        @Test
        fun `given missing book product when place then returns target not found`() {
            val context = createSut()
            every { context.repository.findTarget(BookProductId("missing-book")) } returns null

            val result = context.sut.place(PlaceReservationInput(libraryUserId = "user-1", bookProductId = "missing-book"))

            assertEquals(Err(UsecaseError.ReservationTargetNotFound), result)
            verify(exactly = 0) { context.eventHandler.handle(any()) }
        }

        @Test
        fun `given all eligibility violations when place then returns all unavailable reasons in specification order`() {
            val context = createSut()
            every { context.repository.findTarget(BookProductId("book-1")) } returns target(availableBookItemCount = 0)
            every { context.repository.lockReserver(LibraryUserId("user-1")) } returns true
            every { context.repository.findReserver(LibraryUserId("user-1")) } returns
                reserver(
                    reservedBookProductIds =
                        setOf(
                            BookProductId("book-1"),
                            BookProductId("book-2"),
                            BookProductId("book-3"),
                        ),
                )

            val result = context.sut.place(PlaceReservationInput(libraryUserId = "user-1", bookProductId = "book-1"))

            assertEquals(
                Err(
                    UsecaseError.ReservationUnavailable(
                        listOf(
                            UsecaseError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM,
                            UsecaseError.ReservationUnavailable.Reason.ALREADY_RESERVED_BOOK_PRODUCT,
                            UsecaseError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED,
                        ),
                    ),
                ),
                result,
            )
            verify(exactly = 0) { context.repository.reserveAvailableBookItem(any()) }
            verify(exactly = 0) { context.eventHandler.handle(any()) }
        }

        @Test
        fun `given competing reservation consumes stock when place then returns conflict`() {
            val context = createSut()
            every { context.repository.findTarget(BookProductId("book-1")) } returns target()
            every { context.repository.lockReserver(LibraryUserId("user-1")) } returns true
            every { context.repository.findReserver(LibraryUserId("user-1")) } returns reserver()
            every { context.repository.reserveAvailableBookItem(BookProductId("book-1")) } returns null

            val result = context.sut.place(PlaceReservationInput(libraryUserId = "user-1", bookProductId = "book-1"))

            assertEquals(
                Err(
                    UsecaseError.ReservationUnavailable(
                        listOf(UsecaseError.ReservationUnavailable.Reason.RESERVATION_CONFLICT),
                    ),
                ),
                result,
            )
            verify(exactly = 0) { context.eventHandler.handle(any()) }
        }
    }

    private fun createSut(
        repository: ReservationCommandRepository = mockk(),
        eventHandler: ReservationPlacedEventHandler = mockk(),
    ): TestContext {
        val sut =
            PlaceReservationUseCase(
                reservationRepository = repository,
                reservationEligibility = ReservationEligibility(),
                reservationPlacedEventHandler = eventHandler,
                clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
            )
        return TestContext(sut = sut, repository = repository, eventHandler = eventHandler)
    }

    private fun target(availableBookItemCount: Int = 1): ReservationTargetBookProduct =
        ReservationTargetBookProduct(
            bookProductId = BookProductId("book-1"),
            title = "Kotlin入門",
            isbn = "9780000000001",
            availableBookItemCount = availableBookItemCount,
        )

    private fun reserver(
        reservedBookProductIds: Set<BookProductId> = emptySet(),
    ): Reserver =
        Reserver(
            libraryUserId = LibraryUserId("user-1"),
            reservedBookProductIds = reservedBookProductIds,
        )

    private fun assertReservationPlacedEvent(
        event: ReservationPlacedEvent,
        reservationId: String,
        libraryUserId: String,
        bookProductId: String,
        bookItemId: String,
        occurredAt: Instant,
    ) {
        assertEquals(reservationId, event.reservationId.value)
        assertEquals(libraryUserId, event.libraryUserId.value)
        assertEquals(bookProductId, event.bookProductId.value)
        assertEquals(bookItemId, event.bookItemId.value)
        assertEquals(occurredAt, event.occurredAt)
    }
}
