package jp.glory.practice.agentic.shared.web

import org.springframework.http.HttpStatus

open class ApiException(
    val code: String,
    message: String,
    val status: HttpStatus,
    val details: List<ApiErrorDetail> = emptyList(),
) : RuntimeException(message)

class ValidationApiException(details: List<ApiErrorDetail>) : ApiException(
    code = "VALIDATION_ERROR",
    message = "入力値に誤りがあります。",
    status = HttpStatus.BAD_REQUEST,
    details = details,
)

class DuplicateEmailApiException : ApiException(
    code = "DUPLICATE_EMAIL",
    message = "既に使用されているメールアドレスです。",
    status = HttpStatus.BAD_REQUEST,
)

class UnauthorizedApiException : ApiException(
    code = "UNAUTHORIZED",
    message = "認証に失敗しました。",
    status = HttpStatus.UNAUTHORIZED,
)
