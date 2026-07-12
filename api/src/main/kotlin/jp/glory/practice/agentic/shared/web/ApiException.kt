package jp.glory.practice.agentic.shared.web

import org.springframework.http.HttpStatus

sealed interface ApiErrorCode {
    val value: String
    val message: String
    val status: HttpStatus
}

enum class ValidationApiErrorCode(
    override val value: String,
    override val message: String,
    override val status: HttpStatus,
) : ApiErrorCode {
    VALIDATION_ERROR(
        value = "VALIDATION_ERROR",
        message = "入力値に誤りがあります。",
        status = HttpStatus.BAD_REQUEST,
    ),
}

enum class AuthenticationApiErrorCode(
    override val value: String,
    override val message: String,
    override val status: HttpStatus,
) : ApiErrorCode {
    UNAUTHORIZED(
        value = "UNAUTHORIZED",
        message = "認証に失敗しました。",
        status = HttpStatus.UNAUTHORIZED,
    ),
    LOGIN_REQUIRED(
        value = "LOGIN_REQUIRED",
        message = "ログインが必要です。",
        status = HttpStatus.UNAUTHORIZED,
    ),
}

enum class BusinessApiErrorCode(
    override val value: String,
    override val message: String,
    override val status: HttpStatus,
) : ApiErrorCode {
    DUPLICATE_EMAIL(
        value = "DUPLICATE_EMAIL",
        message = "既に使用されているメールアドレスです。",
        status = HttpStatus.BAD_REQUEST,
    ),
    RESERVATION_TARGET_NOT_FOUND(
        value = "RESERVATION_TARGET_NOT_FOUND",
        message = "予約対象の書誌が見つかりません。",
        status = HttpStatus.NOT_FOUND,
    ),
    RESERVATION_UNAVAILABLE(
        value = "RESERVATION_UNAVAILABLE",
        message = "予約を受け付けられません。",
        status = HttpStatus.CONFLICT,
    ),
}

sealed class ApiException(
    val errorCode: ApiErrorCode,
    val details: List<ApiErrorDetail> = emptyList(),
) : RuntimeException(errorCode.message) {
    val code: String = errorCode.value
    val status: HttpStatus = errorCode.status
}

class ValidationApiException(
    details: List<ApiErrorDetail>,
) : ApiException(
        errorCode = ValidationApiErrorCode.VALIDATION_ERROR,
        details = details,
    )

class AuthenticationApiException(
    errorCode: AuthenticationApiErrorCode,
) : ApiException(
        errorCode = errorCode,
    )

class BusinessApiException(
    errorCode: BusinessApiErrorCode,
    details: List<ApiErrorDetail> = emptyList(),
) : ApiException(
        errorCode = errorCode,
        details = details,
    )
