package jp.glory.practice.agentic.auth.command.web

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.usecase.LogoutUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import jp.glory.practice.agentic.shared.auth.AccessTokenSession
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import jp.glory.practice.agentic.shared.web.AuthenticationApiErrorCode
import jp.glory.practice.agentic.shared.web.AuthenticationApiException
import org.junit.jupiter.api.Nested
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogoutControllerTest {
    private data class TestContext(
        val mvc: MockMvc,
        val authenticator: AccessTokenAuthenticator,
        val useCase: LogoutUseCase,
    )

    @Nested
    inner class Logout {
        @Test
        fun `given valid bearer when post logout then returns 204`() {
            val context = createSut()
            every { context.authenticator.requireValidToken("Bearer token-123") } returns
                AccessTokenSession(
                    token = "token-123",
                    libraryUserId = "user-id",
                    expiresAt = Instant.parse("2026-02-23T12:34:56Z"),
                )
            every { context.useCase.logout(any()) } returns Unit

            val response =
                context.mvc
                    .perform(
                        post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer token-123"),
                    ).andReturn()
                    .response

            assertEquals(204, response.status)
            assertTrue(response.contentAsString.isEmpty())
            verify(exactly = 1) {
                context.useCase.logout(
                    match { it.libraryUserId == "user-id" && it.accessToken == "token-123" },
                )
            }
        }

        @Test
        fun `given missing bearer when post logout then returns 401`() {
            val context = createSut()
            every { context.authenticator.requireValidToken(null) } throws
                AuthenticationApiException(AuthenticationApiErrorCode.LOGIN_REQUIRED)

            val response =
                context.mvc
                    .perform(post("/api/v1/auth/logout"))
                    .andReturn()
                    .response

            assertEquals(401, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"LOGIN_REQUIRED\""))
            assertTrue(response.contentAsString.contains("\"details\":[]"))
            assertTrue(response.contentAsString.contains("\"trace_id\":\""))
            verify(exactly = 0) { context.useCase.logout(any()) }
        }
    }

    private fun createSut(
        authenticator: AccessTokenAuthenticator = mockk(),
        useCase: LogoutUseCase = mockk(),
    ): TestContext {
        val mvc =
            MockMvcBuilders
                .standaloneSetup(LogoutController(authenticator, useCase))
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
        return TestContext(mvc = mvc, authenticator = authenticator, useCase = useCase)
    }
}
