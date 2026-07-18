package jp.glory.practice.agentic.reservation.command.web

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.reservation.command.usecase.PlaceReservationResult
import jp.glory.practice.agentic.reservation.command.usecase.PlaceReservationUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import jp.glory.practice.agentic.shared.auth.AccessTokenSession
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import jp.glory.practice.agentic.shared.web.AuthenticationApiErrorCode
import jp.glory.practice.agentic.shared.web.AuthenticationApiException
import org.junit.jupiter.api.Nested
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlaceReservationControllerTest {
    private data class TestContext(
        val mvc: MockMvc,
        val useCase: PlaceReservationUseCase,
        val authenticator: AccessTokenAuthenticator,
    )

    @Nested
    inner class Place {
        @Test
        fun `given valid request when post reservations then returns 201`() {
            val context = createSut()
            every { context.authenticator.requireValidToken("Bearer token-123") } returns session()
            every { context.useCase.place(any()) } returns
                Ok(
                    PlaceReservationResult(
                        reservationId = "reservation-1",
                        bookProductId = "book-1",
                        title = "Kotlin入門",
                        isbn = "9780000000001",
                        bookItemId = "item-1",
                        reservedAt = Instant.parse("2026-02-22T12:34:56Z"),
                        eventName = "ReservationPlacedEvent",
                    ),
                )

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/reservations")
                            .header("Authorization", "Bearer token-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""{"book_product_id":"book-1"}"""),
                    ).andReturn()
                    .response

            assertEquals(201, response.status)
            assertTrue(response.contentAsString.contains("\"reservation_id\":\"reservation-1\""))
            assertTrue(response.contentAsString.contains("\"book_product_id\":\"book-1\""))
            assertTrue(response.contentAsString.contains("\"book_item_id\":\"item-1\""))
            verify(exactly = 1) {
                context.useCase.place(
                    match {
                        it.libraryUserId == "user-1" && it.bookProductId == "book-1"
                    },
                )
            }
        }

        @Test
        fun `given unauthenticated request when post reservations then returns 401`() {
            val context = createSut()
            every { context.authenticator.requireValidToken(null) } throws
                AuthenticationApiException(AuthenticationApiErrorCode.LOGIN_REQUIRED)

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""{"book_product_id":"book-1"}"""),
                    ).andReturn()
                    .response

            assertEquals(401, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"LOGIN_REQUIRED\""))
            verify(exactly = 0) { context.useCase.place(any()) }
        }

        @Test
        fun `given invalid request when post reservations then returns 400`() {
            val context = createSut()
            every { context.authenticator.requireValidToken("Bearer token-123") } returns session()

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/reservations")
                            .header("Authorization", "Bearer token-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""{"book_product_id":""}"""),
                    ).andReturn()
                    .response

            assertEquals(400, response.status)
            assertTrue(response.contentAsString.contains("\"field\":\"book_product_id\""))
            verify(exactly = 0) { context.useCase.place(any()) }
        }

        @Test
        fun `given target not found when post reservations then returns 404`() {
            val context = createSut()
            every { context.authenticator.requireValidToken("Bearer token-123") } returns session()
            every { context.useCase.place(any()) } returns Err(UsecaseError.ReservationTargetNotFound)

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/reservations")
                            .header("Authorization", "Bearer token-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""{"book_product_id":"missing-book"}"""),
                    ).andReturn()
                    .response

            assertEquals(404, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"RESERVATION_TARGET_NOT_FOUND\""))
        }

        @Test
        fun `given unavailable reservation when post reservations then returns 409`() {
            val context = createSut()
            every { context.authenticator.requireValidToken("Bearer token-123") } returns session()
            every { context.useCase.place(any()) } returns
                Err(
                    UsecaseError.ReservationUnavailable(
                        listOf(UsecaseError.ReservationUnavailable.Reason.NO_AVAILABLE_BOOK_ITEM),
                    ),
                )

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/reservations")
                            .header("Authorization", "Bearer token-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""{"book_product_id":"book-1"}"""),
                    ).andReturn()
                    .response

            assertEquals(409, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"RESERVATION_UNAVAILABLE\""))
            assertTrue(response.contentAsString.contains("\"reason\":\"no_available_book_item\""))
        }
    }

    private fun createSut(
        useCase: PlaceReservationUseCase = mockk(),
        authenticator: AccessTokenAuthenticator = mockk(),
    ): TestContext {
        val builder: StandaloneMockMvcBuilder =
            MockMvcBuilders
                .standaloneSetup(
                    PlaceReservationController(
                        validator = PlaceReservationRequestValidator(),
                        useCase = useCase,
                        authenticator = authenticator,
                    ),
                )
        builder.setControllerAdvice(GlobalExceptionHandler())
        return TestContext(mvc = builder.build(), useCase = useCase, authenticator = authenticator)
    }

    private fun session(): AccessTokenSession =
        AccessTokenSession(
            token = "token-123",
            libraryUserId = "user-1",
            expiresAt = Instant.parse("2026-02-22T13:34:56Z"),
        )
}
