package jp.glory.practice.agentic.reservation.command.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.reservation.command.usecase.PlaceReservationInput
import jp.glory.practice.agentic.reservation.command.usecase.PlaceReservationUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import jp.glory.practice.agentic.shared.web.toApiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/reservations")
class PlaceReservationController(
    private val validator: PlaceReservationRequestValidator,
    private val useCase: PlaceReservationUseCase,
    private val authenticator: AccessTokenAuthenticator,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun place(
        @RequestHeader("Authorization", required = false) authorization: String?,
        @RequestBody request: PlaceReservationRequest,
    ): PlaceReservationResponse {
        val session = authenticator.requireValidToken(authorization)
        validator.validateOrThrow(request)

        return useCase
            .place(
                PlaceReservationInput(
                    libraryUserId = session.libraryUserId,
                    bookProductId = request.bookProductId!!,
                ),
            ).fold(
                success = {
                    PlaceReservationResponse(
                        reservationId = it.reservationId,
                        bookProductId = it.bookProductId,
                        title = it.title,
                        isbn = it.isbn,
                        bookItemId = it.bookItemId,
                        reservedAt = it.reservedAt,
                        eventName = it.eventName,
                    )
                },
                failure = { throw toApiException(it) },
            )
    }
}

data class PlaceReservationRequest(
    @JsonProperty("book_product_id")
    val bookProductId: String? = null,
)

data class PlaceReservationResponse(
    @JsonProperty("reservation_id")
    val reservationId: String,
    @JsonProperty("book_product_id")
    val bookProductId: String,
    val title: String,
    val isbn: String,
    @JsonProperty("book_item_id")
    val bookItemId: String,
    @JsonProperty("reserved_at")
    val reservedAt: Instant,
    @JsonProperty("event_name")
    val eventName: String,
)
