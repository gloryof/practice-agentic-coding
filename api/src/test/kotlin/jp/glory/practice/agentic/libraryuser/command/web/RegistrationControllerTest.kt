package jp.glory.practice.agentic.libraryuser.command.web

import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.libraryuser.command.usecase.RegisterLibraryUserUseCase
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class RegistrationControllerTest {
    @Test
    fun `returns 201 on success`() {
        val repo = InMemoryUserRepository(false)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(repo),
            libraryUserRepository = repo,
            authCredentialRepository = InMemoryCredentialRepository(),
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/library-users/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"user@example.com","password":"Str0ng!Passw0rd"}""")
        ).andReturn().response

        assertEquals(201, response.status)
        assertTrue(response.contentAsString.contains("library_user_id"))
    }

    @Test
    fun `returns 400 on validation error`() {
        val repo = InMemoryUserRepository(false)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(repo),
            libraryUserRepository = repo,
            authCredentialRepository = InMemoryCredentialRepository(),
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/library-users/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"","password":"short"}""")
        ).andReturn().response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
        assertTrue(response.contentAsString.contains("\"trace_id\":\""))
    }

    @Test
    fun `returns 400 on duplicate email`() {
        val repo = InMemoryUserRepository(true)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(repo),
            libraryUserRepository = repo,
            authCredentialRepository = InMemoryCredentialRepository(),
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )
        val mvc = buildMockMvc(useCase)

        val response = mvc.perform(
            post("/api/v1/library-users/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"user@example.com","password":"Str0ng!Passw0rd"}""")
        ).andReturn().response

        assertEquals(400, response.status)
        assertTrue(response.contentAsString.contains("\"code\":\"DUPLICATE_EMAIL\""))
    }

    private fun buildMockMvc(useCase: RegisterLibraryUserUseCase): MockMvc {
        val builder: StandaloneMockMvcBuilder = MockMvcBuilders
            .standaloneSetup(RegistrationController(RegistrationRequestValidator(), useCase))
        builder.setControllerAdvice(GlobalExceptionHandler())
        return builder.build()
    }

    private class InMemoryUserRepository(private val duplicated: Boolean) : LibraryUserCommandRepository {
        override fun save(event: LibraryUserRegisteredEvent) = Unit

        override fun existsByEmail(email: Email): Boolean = duplicated
    }

    private class InMemoryCredentialRepository : AuthCredentialRepository {
        override fun save(libraryUserId: String, passwordHash: String) = Unit

        override fun findByLibraryUserId(libraryUserId: String) = null
    }
}
