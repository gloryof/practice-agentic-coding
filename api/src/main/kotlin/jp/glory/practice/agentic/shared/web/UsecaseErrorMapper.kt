package jp.glory.practice.agentic.shared.web

import jp.glory.practice.agentic.shared.usecase.UsecaseError

fun toApiException(error: UsecaseError): RuntimeException {
    return when (error) {
        is UsecaseError.Validation -> ValidationApiException(
            listOf(ApiErrorDetail(field = error.field, reason = error.reason))
        )

        UsecaseError.DuplicateEmail -> DuplicateEmailApiException()
        UsecaseError.AuthenticationFailed -> UnauthorizedApiException()
    }
}
