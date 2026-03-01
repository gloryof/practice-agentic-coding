package jp.glory.practice.agentic.auth.command.web

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import jp.glory.practice.agentic.auth.command.usecase.LoginUseCase
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginControllerTest {
    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )

    @Test
    fun `returns 200 on success`() {
        val useCase = LoginUseCase(
            authAccountRepository = object : AuthAccountRepository {
                override fun findByEmail(email: Email) = AuthAccount(LibraryUserId("id-1"), email)
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(credential: AuthCredential) = Unit
                override fun findByLibraryUserId(libraryUserId: LibraryUserId) =
                    AuthCredential(LibraryUserId("id-1"), hashed("hashed"))
            },
            passwordVerifier = PasswordVerifier { raw, hash -> raw == "Str0ng!Passw0rd" && hash.value == "hashed" },
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
                override fun findByEmail(email: Email) = AuthAccount(LibraryUserId("id-1"), email)
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(credential: AuthCredential) = Unit
                override fun findByLibraryUserId(libraryUserId: LibraryUserId) =
                    AuthCredential(LibraryUserId("id-1"), hashed("hashed"))
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
                override fun findByEmail(email: Email) = AuthAccount(LibraryUserId("id-1"), email)
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(credential: AuthCredential) = Unit
                override fun findByLibraryUserId(libraryUserId: LibraryUserId) =
                    AuthCredential(LibraryUserId("id-1"), hashed("hashed"))
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
