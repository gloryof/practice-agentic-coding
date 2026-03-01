package jp.glory.practice.agentic.auth.command.web

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import jp.glory.practice.agentic.auth.command.usecase.LoginUseCase
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginControllerTest {
    @Test
    fun `returns 200 on success`() {
        val useCase = LoginUseCase(
            authAccountRepository = object : AuthAccountRepository {
                override fun findByEmail(email: String) = AuthAccount("id-1", "user@example.com")
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(libraryUserId: String, passwordHash: String) = Unit
                override fun findByLibraryUserId(libraryUserId: String) = AuthCredential("id-1", "hashed")
            },
            passwordVerifier = PasswordVerifier { raw, hash -> raw == "Str0ng!Passw0rd" && hash == "hashed" },
            accessTokenGenerator = AccessTokenGenerator { "token-123" },
            expirationSeconds = 86400,
        )
        val builder: StandaloneMockMvcBuilder = MockMvcBuilders
            .standaloneSetup(LoginController(LoginRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        val mvc = builder.build()

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
        val useCase = LoginUseCase(
            authAccountRepository = object : AuthAccountRepository {
                override fun findByEmail(email: String) = AuthAccount("id-1", "user@example.com")
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(libraryUserId: String, passwordHash: String) = Unit
                override fun findByLibraryUserId(libraryUserId: String) = AuthCredential("id-1", "hashed")
            },
            passwordVerifier = PasswordVerifier { _, _ -> false },
            accessTokenGenerator = AccessTokenGenerator { "token-123" },
            expirationSeconds = 86400,
        )
        val builder: StandaloneMockMvcBuilder = MockMvcBuilders
            .standaloneSetup(LoginController(LoginRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        val mvc = builder.build()

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
        val useCase = LoginUseCase(
            authAccountRepository = object : AuthAccountRepository {
                override fun findByEmail(email: String) = AuthAccount("id-1", "user@example.com")
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(libraryUserId: String, passwordHash: String) = Unit
                override fun findByLibraryUserId(libraryUserId: String) = AuthCredential("id-1", "hashed")
            },
            passwordVerifier = PasswordVerifier { _, _ -> true },
            accessTokenGenerator = AccessTokenGenerator { "token-123" },
            expirationSeconds = 86400,
        )
        val builder: StandaloneMockMvcBuilder = MockMvcBuilders
            .standaloneSetup(LoginController(LoginRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        val mvc = builder.build()

        val response = mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"","password":"short"}""")
        ).andReturn().response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
        assertTrue(response.contentAsString.contains("\"details\":["))
        assertTrue(response.contentAsString.contains("\"trace_id\":\""))
    }
}
