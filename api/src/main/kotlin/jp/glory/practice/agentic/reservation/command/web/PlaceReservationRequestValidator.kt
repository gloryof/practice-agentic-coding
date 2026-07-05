package jp.glory.practice.agentic.reservation.command.web

import jp.glory.practice.agentic.shared.web.ApiErrorDetail
import jp.glory.practice.agentic.shared.web.ValidationApiException
import org.springframework.stereotype.Component

@Component
class PlaceReservationRequestValidator {
    fun validateOrThrow(request: PlaceReservationRequest) {
        val details = mutableListOf<ApiErrorDetail>()
        if (request.bookProductId.isNullOrBlank()) {
            details.add(ApiErrorDetail(field = "book_product_id", reason = "required"))
        }
        if (details.isNotEmpty()) {
            throw ValidationApiException(details)
        }
    }
}
