package jp.glory.practice.agentic.libraryuser.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class RegisterLibraryUserUseCaseTest {
    @Test
    fun `registers user and credential`() {
        val userRepository = InMemoryLibraryUserRepository(false)
        val credentialRepository = InMemoryAuthCredentialRepository()
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = credentialRepository,
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        result.fold(
            success = {
                assertEquals("user@example.com", it.email)
                assertEquals("LibraryUserRegisteredEvent", it.eventName)
                assertEquals(1, userRepository.userIds.size)
                assertEquals("hashed-Str0ng!Passw0rd", credentialRepository.credentials[it.libraryUserId])
            },
            failure = { error("expected success") },
        )
    }

    @Test
    fun `returns err on duplicate email`() {
        val userRepository = InMemoryLibraryUserRepository(true)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = InMemoryAuthCredentialRepository(),
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        assertEquals(Err(UsecaseError.DuplicateEmail), result)
    }

    @Test
    fun `returns validation error on invalid password`() {
        val userRepository = InMemoryLibraryUserRepository(false)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = InMemoryAuthCredentialRepository(),
            passwordHasher = PasswordHasher { "hashed-$it" },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "short"))
        assertEquals(
            Err(UsecaseError.Validation(field = "password", reason = "must_meet_password_policy")),
            result
        )
    }

    private class InMemoryLibraryUserRepository(private val existsByEmail: Boolean) : LibraryUserCommandRepository {
        val userIds = mutableListOf<LibraryUserId>()

        override fun save(event: LibraryUserRegisteredEvent) {
            userIds.add(event.libraryUserId)
        }

        override fun existsByEmail(email: Email): Boolean = existsByEmail
    }

    private class InMemoryAuthCredentialRepository : AuthCredentialRepository {
        val credentials = mutableMapOf<String, String>()

        override fun save(libraryUserId: String, passwordHash: String) {
            credentials[libraryUserId] = passwordHash
        }

        override fun findByLibraryUserId(libraryUserId: String) = null
    }
}
