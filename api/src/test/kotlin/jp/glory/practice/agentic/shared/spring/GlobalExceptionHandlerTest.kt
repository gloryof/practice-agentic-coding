package jp.glory.practice.agentic.shared.spring

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import jakarta.servlet.http.HttpServletRequest
import jp.glory.practice.agentic.shared.web.ApiErrorDetail
import jp.glory.practice.agentic.shared.web.ApiException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GlobalExceptionHandlerTest {
    @AfterTest
    fun tearDown() {
        MDC.remove(TraceIdFilter.TRACE_ID_MDC_KEY)
    }

    @Test
    fun `logs error with traceId and http info on unexpected exception`() {
        val handler = GlobalExceptionHandler()
        val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        logger.addAppender(appender)

        try {
            MDC.put(TraceIdFilter.TRACE_ID_MDC_KEY, "trace-123")
            val request: HttpServletRequest = MockHttpServletRequest("POST", "/api/v1/test")

            val response = handler.handleUnexpectedException(IllegalStateException("boom"), request)

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
            assertTrue(logMessage.contains("path=/api/v1/test"))
            assertTrue(event.throwableProxy?.message?.contains("boom") == true)
        } finally {
            logger.detachAppender(appender)
            appender.stop()
        }
    }

    @Test
    fun `does not log on api exception`() {
        val handler = GlobalExceptionHandler()
        val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        logger.addAppender(appender)

        try {
            MDC.put(TraceIdFilter.TRACE_ID_MDC_KEY, "trace-456")
            val apiException = ApiException(
                status = HttpStatus.BAD_REQUEST,
                code = "VALIDATION_ERROR",
                message = "invalid",
                details = listOf(ApiErrorDetail("field", "invalid"))
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
