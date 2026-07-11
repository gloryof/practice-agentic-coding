package jp.glory.practice.agentic.reservation.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.reservation.command.domain.constraint.ReservationEligibility
import jp.glory.practice.agentic.reservation.command.domain.constraint.ReservationEligibilityViolation
import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEvent
import jp.glory.practice.agentic.reservation.command.domain.event.ReservationPlacedEventHandler
import jp.glory.practice.agentic.reservation.command.domain.model.BookItemId
import jp.glory.practice.agentic.reservation.command.domain.model.BookProductId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationId
import jp.glory.practice.agentic.reservation.command.domain.model.ReservationTargetBookProduct
import jp.glory.practice.agentic.reservation.command.domain.repository.ReservationCommandRepository
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class PlaceReservationUseCase(
    private val reservationRepository: ReservationCommandRepository,
    private val reservationEligibility: ReservationEligibility,
    private val reservationPlacedEventHandler: ReservationPlacedEventHandler,
    private val clock: Clock,
) {
    private data class ReservationRequest(
        val libraryUserId: LibraryUserId,
        val bookProductId: BookProductId,
    )

    private data class ReservationPreparation(
        val request: ReservationRequest,
        val target: ReservationTargetBookProduct,
    )

    private data class ReservedBookItem(
        val preparation: ReservationPreparation,
        val bookItemId: BookItemId,
    )

    private data class ReservationPlacement(
        val preparation: ReservationPreparation,
        val event: ReservationPlacedEvent,
    )

    @Transactional
    fun place(input: PlaceReservationInput): Result<PlaceReservationResult, UsecaseError> =
        Ok(createReservationRequest(input))
            .andThen(::prepareReservation)
            .andThen(::reserveBookItem)
            .map(::recordReservation)
            .map(::buildResult)

    private fun createReservationRequest(input: PlaceReservationInput): ReservationRequest =
        ReservationRequest(
            libraryUserId = LibraryUserId(input.libraryUserId),
            bookProductId = BookProductId(input.bookProductId.trim()),
        )

    private fun prepareReservation(request: ReservationRequest): Result<ReservationPreparation, UsecaseError> {
        val target =
            reservationRepository.findTarget(request.bookProductId)
                ?: return Err(UsecaseError.ReservationTargetNotFound)
        val reserver = lockAndFindReserver(request.libraryUserId)
        val eligibility = reservationEligibility.evaluate(reserver, target)
        if (!eligibility.isSatisfied()) {
            return Err(UsecaseError.ReservationUnavailable(eligibility.violations.map(::toUnavailableReason)))
        }

        return Ok(ReservationPreparation(request = request, target = target))
    }

    // 同一利用者の並行予約で古い予約一覧を使って予約上限・重複予約判定を通過しないよう、
    // 利用者行をロックアンカーとして予約可否判定から保存までを直列化する。
    private fun lockAndFindReserver(libraryUserId: LibraryUserId) =
        reservationRepository
            .also { it.lockReserver(libraryUserId) }
            .findReserver(libraryUserId)

    private fun reserveBookItem(preparation: ReservationPreparation): Result<ReservedBookItem, UsecaseError> {
        val bookItemId =
            reservationRepository.reserveAvailableBookItem(preparation.request.bookProductId)
                ?: return Err(
                    UsecaseError.ReservationUnavailable(
                        listOf(UsecaseError.ReservationUnavailable.Reason.RESERVATION_CONFLICT),
                    ),
                )

        return Ok(ReservedBookItem(preparation = preparation, bookItemId = bookItemId))
    }

    private fun toUnavailableReason(
        violation: ReservationEligibilityViolation,
    ): UsecaseError.ReservationUnavailable.Reason =
        when (violation) {
            ReservationEligibilityViolation.NO_AVAILABLE_BOOK_ITEM ->
                UsecaseError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM
            ReservationEligibilityViolation.ALREADY_RESERVED_BOOK_PRODUCT ->
                UsecaseError.ReservationUnavailable.Reason.ALREADY_RESERVED_BOOK_PRODUCT
            ReservationEligibilityViolation.RESERVATION_LIMIT_REACHED ->
                UsecaseError.ReservationUnavailable.Reason.RESERVATION_LIMIT_REACHED
        }

    private fun recordReservation(reservedBookItem: ReservedBookItem): ReservationPlacement {
        val request = reservedBookItem.preparation.request
        val event =
            buildEvent(
                libraryUserId = request.libraryUserId,
                bookProductId = request.bookProductId,
                bookItemId = reservedBookItem.bookItemId,
            )
        reservationPlacedEventHandler.handle(event)

        return ReservationPlacement(
            preparation = reservedBookItem.preparation,
            event = event,
        )
    }

    private fun buildEvent(
        libraryUserId: LibraryUserId,
        bookProductId: BookProductId,
        bookItemId: BookItemId,
    ): ReservationPlacedEvent =
        ReservationPlacedEvent(
            reservationId = ReservationId.issue(),
            libraryUserId = libraryUserId,
            bookProductId = bookProductId,
            bookItemId = bookItemId,
            occurredAt = Instant.now(clock),
        )

    private fun buildResult(placement: ReservationPlacement): PlaceReservationResult =
        PlaceReservationResult(
            reservationId = placement.event.reservationId.value,
            bookProductId = placement.event.bookProductId.value,
            title = placement.preparation.target.title,
            isbn = placement.preparation.target.isbn,
            bookItemId = placement.event.bookItemId.value,
            reservedAt = placement.event.occurredAt,
            eventName = placement.event::class.simpleName ?: "ReservationPlacedEvent",
        )
}

data class PlaceReservationInput(
    val libraryUserId: String,
    val bookProductId: String,
)

data class PlaceReservationResult(
    val reservationId: String,
    val bookProductId: String,
    val title: String,
    val isbn: String,
    val bookItemId: String,
    val reservedAt: Instant,
    val eventName: String,
)
