package jp.glory.practice.agentic.shared.web

import jp.glory.practice.agentic.shared.usecase.UsecaseError

fun toApiException(error: UsecaseError): RuntimeException =
    when (error) {
        is UsecaseError.Validation ->
            ValidationApiException(
                listOf(ApiErrorDetail(field = error.field, reason = error.reason)),
            )

        UsecaseError.DuplicateEmail -> DuplicateEmailApiException()
        UsecaseError.AuthenticationFailed -> UnauthorizedApiException()
        UsecaseError.ReservationTargetNotFound -> ReservationTargetNotFoundApiException()
        is UsecaseError.ReservationUnavailable ->
            ReservationUnavailableApiException(
                error.reasons.map { ApiErrorDetail(field = "reservation", reason = it.code) },
            )
    }
