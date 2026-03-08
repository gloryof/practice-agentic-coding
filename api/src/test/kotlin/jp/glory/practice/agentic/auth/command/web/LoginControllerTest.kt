package jp.glory.practice.agentic.auth.command.web

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.usecase.LoginResult
import jp.glory.practice.agentic.auth.command.usecase.LoginUseCase
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginControllerTest {
    @Test
    fun `returns 200 on success`() {
        val useCase = mockk<LoginUseCase>()
        every { useCase.login(any()) } returns Ok(
            LoginResult(
                accessToken = "token-123",
                tokenType = "Bearer",
                expiresInSeconds = 86400,
            )
        )
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"user@example.com","password":"Str0ng!Passw0rd"}""")
        ).andReturn().response

        assertEquals(200, response.status)
        assertTrue(response.contentAsString.contains("access_token"))
    }

    @Test
    fun `returns 401 on auth failure`() {
        val useCase = mockk<LoginUseCase>()
        every { useCase.login(any()) } returns Err(UsecaseError.AuthenticationFailed)
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"user@example.com","password":"Str0ng!Wrong12"}""")
        ).andReturn().response

        assertEquals(401, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"UNAUTHORIZED\""))
        assertTrue(response.contentAsString.contains("\"trace_id\":\""))
    }

    @Test
    fun `returns 400 on validation error`() {
        val useCase = mockk<LoginUseCase>()
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"","password":"short"}""")
        ).andReturn().response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
        assertTrue(response.contentAsString.contains("\"details\":["))
        assertTrue(response.contentAsString.contains("\"trace_id\":\""))
        verify(exactly = 0) { useCase.login(any()) }
    }

    private fun buildMockMvc(useCase: LoginUseCase): MockMvc {
        val builder: StandaloneMockMvcBuilder = MockMvcBuilders
            .standaloneSetup(LoginController(LoginRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        return builder.build()
    }
}
