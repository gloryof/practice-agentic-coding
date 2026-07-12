package jp.glory.practice.agentic.shared.web

import jp.glory.practice.agentic.shared.usecase.UsecaseError

fun toApiException(error: UsecaseError): ApiException =
    when (error) {
        is UsecaseError.Validation ->
            ValidationApiException(
                listOf(ApiErrorDetail(field = error.field, reason = error.reason)),
            )

        UsecaseError.DuplicateEmail -> BusinessApiException(BusinessApiErrorCode.DUPLICATE_EMAIL)
        UsecaseError.AuthenticationFailed -> AuthenticationApiException(AuthenticationApiErrorCode.UNAUTHORIZED)
        UsecaseError.ReservationTargetNotFound ->
            BusinessApiException(BusinessApiErrorCode.RESERVATION_TARGET_NOT_FOUND)
        is UsecaseError.ReservationUnavailable ->
            BusinessApiException(
                errorCode = BusinessApiErrorCode.RESERVATION_UNAVAILABLE,
                details = error.reasons.map { ApiErrorDetail(field = "reservation", reason = it.code) },
            )
    }
