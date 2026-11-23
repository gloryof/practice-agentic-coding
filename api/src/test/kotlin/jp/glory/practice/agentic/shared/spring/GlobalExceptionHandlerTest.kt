package jp.glory.practice.agentic.shared.spring

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import jp.glory.practice.agentic.shared.web.ApiErrorDetail
import jp.glory.practice.agentic.shared.web.BusinessApiErrorCode
import jp.glory.practice.agentic.shared.web.BusinessApiException
import org.junit.jupiter.api.Nested
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.HandlerMapping
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GlobalExceptionHandlerTest {
    @AfterTest
    fun tearDown() {
        MDC.remove(TraceIdFilter.TRACE_ID_MDC_KEY)
    }

    @Nested
    inner class HandleException {
        @Test
        fun `given sensitive exception and path when handle then logs stack frames without sensitive messages`() {
            val handler = GlobalExceptionHandler()
            val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) as Logger
            val appender = ListAppender<ILoggingEvent>()
            appender.start()
            logger.addAppender(appender)

            try {
                MDC.put(TraceIdFilter.TRACE_ID_MDC_KEY, "trace-123")
                val sensitive = "synthetic-reader@example.com"
                val request = MockHttpServletRequest("POST", "/api/v1/test/$sensitive")
                request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/v1/test/{id}")

                val cause = IllegalArgumentException("synthetic-password")
                cause.stackTrace = arrayOf(StackTraceElement("example.Repository", "save", "Repository.kt", 42))
                val exception = IllegalStateException(sensitive, cause)
                exception.stackTrace = arrayOf(StackTraceElement("example.Service", "execute", "Service.kt", 10))
                val suppressed = IllegalStateException("synthetic-suppressed-secret")
                suppressed.stackTrace = arrayOf(StackTraceElement("example.Resource", "close", "Resource.kt", 7))
                exception.addSuppressed(suppressed)
                // 原因例外が循環していてもログ整形が終了することを確認する。
                cause.initCause(exception)

                val response = handler.handleUnexpectedException(exception, request)

                assertEquals(500, response.statusCode.value())
                assertEquals("INTERNAL_SERVER_ERROR", response.body?.code)
                assertEquals("trace-123", response.body?.traceId)

                val errorLogs = appender.list.filter { it.level == Level.ERROR }
                assertEquals(1, errorLogs.size)

                val event = errorLogs.first()
                val logMessage = event.formattedMessage
                assertTrue(logMessage.contains("Unhandled exception occurred"))
                assertTrue(logMessage.contains("traceId=trace-123"))
                assertTrue(logMessage.contains("method=POST"))
                assertTrue(logMessage.contains("route=/api/v1/test/{id}"))
                assertTrue(logMessage.contains("exceptionClass=java.lang.IllegalStateException"))
                assertFalse(logMessage.contains(sensitive))
                assertFalse(logMessage.contains("synthetic-password"))
                assertFalse(logMessage.contains("synthetic-suppressed-secret"))
                assertTrue(logMessage.contains("at example.Service.execute(Service.kt:10)"))
                assertTrue(logMessage.contains("Caused by: java.lang.IllegalArgumentException"))
                assertTrue(logMessage.contains("at example.Repository.save(Repository.kt:42)"))
                assertTrue(logMessage.contains("Suppressed: java.lang.IllegalStateException"))
                assertTrue(logMessage.contains("at example.Resource.close(Resource.kt:7)"))
                assertTrue(logMessage.contains("[circular reference: java.lang.IllegalStateException]"))
                assertNull(event.throwableProxy)
                assertFalse(response.body.toString().contains(sensitive))
            } finally {
                logger.detachAppender(appender)
                appender.stop()
            }
        }

        @Test
        fun `given api exception when handle then does not log error`() {
            val handler = GlobalExceptionHandler()
            val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) as Logger
            val appender = ListAppender<ILoggingEvent>()
            appender.start()
            logger.addAppender(appender)

            try {
                MDC.put(TraceIdFilter.TRACE_ID_MDC_KEY, "trace-456")
                val apiException =
                    BusinessApiException(
                        errorCode = BusinessApiErrorCode.DUPLICATE_EMAIL,
                        details = listOf(ApiErrorDetail("field", "invalid")),
                    )

                val response = handler.handleApiException(apiException)

                assertEquals(400, response.statusCode.value())
                assertEquals("trace-456", response.body?.traceId)
                assertFalse(appender.list.any { it.level == Level.ERROR })
            } finally {
                logger.detachAppender(appender)
                appender.stop()
            }
        }
    }
}
