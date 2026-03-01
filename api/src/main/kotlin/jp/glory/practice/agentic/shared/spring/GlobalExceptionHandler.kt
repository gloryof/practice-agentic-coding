package jp.glory.practice.agentic.shared.spring

import jp.glory.practice.agentic.shared.web.ApiError
import jp.glory.practice.agentic.shared.web.ApiException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(ex.status)
            .body(
                ApiError(
                    code = ex.code,
                    message = ex.message ?: "",
                    details = ex.details,
                    traceId = currentTraceId(),
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        logger.error(
            "Unhandled exception occurred. traceId={}, method={}, path={}",
            currentTraceId(),
            request.method,
            request.requestURI,
            ex
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiError(
                    code = "INTERNAL_SERVER_ERROR",
                    message = "システムエラーが発生しました。",
                    details = emptyList(),
                    traceId = currentTraceId(),
                )
            )
    }

    private fun currentTraceId(): String {
        return MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY) ?: ""
    }
}
