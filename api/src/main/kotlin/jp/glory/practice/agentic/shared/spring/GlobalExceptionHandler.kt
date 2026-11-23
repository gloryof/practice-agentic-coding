package jp.glory.practice.agentic.shared.spring

import jakarta.servlet.http.HttpServletRequest
import jp.glory.practice.agentic.shared.web.ApiError
import jp.glory.practice.agentic.shared.web.ApiException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.HandlerMapping
import java.util.Collections
import java.util.IdentityHashMap

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiError> =
        ResponseEntity
            .status(ex.status)
            .body(
                ApiError(
                    code = ex.code,
                    message = ex.message ?: "",
                    details = ex.details,
                    traceId = currentTraceId(),
                ),
            )

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiError> {
        logger.error(
            "Unhandled exception occurred. traceId={}, method={}, route={}, exceptionClass={}\n{}",
            currentTraceId(),
            request.method,
            request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE),
            ex.javaClass.name,
            formatStackTrace(ex),
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiError(
                    code = "INTERNAL_SERVER_ERROR",
                    message = "システムエラーが発生しました。",
                    details = emptyList(),
                    traceId = currentTraceId(),
                ),
            )
    }

    private fun currentTraceId(): String = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY) ?: ""

    // 例外をそのままロガーへ渡すと、スタック情報とともに例外メッセージも出力される。
    // メッセージには入力値・個人情報・認証情報が含まれ得るため、ここでメッセージを除外する。
    // 障害調査に必要なクラス・メソッド・ファイル・行番号と、原因・抑制例外の経路は残す。
    private fun formatStackTrace(exception: Throwable): String {
        val visited = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
        return buildString {
            fun appendException(
                error: Throwable,
                caption: String,
            ) {
                if (!visited.add(error)) {
                    appendLine("$caption[circular reference: ${error.javaClass.name}]")
                    return
                }
                // 例外メッセージは含めず、原因・抑制例外の呼び出し経路も残す。
                appendLine("$caption${error.javaClass.name}")
                error.stackTrace.forEach { appendLine("\tat $it") }
                error.suppressed.forEach { appendException(it, "Suppressed: ") }
                error.cause?.let { appendException(it, "Caused by: ") }
            }
            appendException(exception, "")
        }
    }
}
