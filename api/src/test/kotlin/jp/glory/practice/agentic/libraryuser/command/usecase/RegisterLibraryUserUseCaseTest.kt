package jp.glory.practice.agentic.libraryuser.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
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
    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )

    @Test
    fun `registers user and credential`() {
        val userRepository = InMemoryLibraryUserRepository(false)
        val credentialRepository = InMemoryAuthCredentialRepository()
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = credentialRepository,
            passwordHasher = PasswordHasher { hashed("hashed-$it") },
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        result.fold(
            success = {
                assertEquals("user@example.com", it.email)
                assertEquals("LibraryUserRegisteredEvent", it.eventName)
                assertEquals(1, userRepository.userIds.size)
                assertEquals(
                    hashed("hashed-Str0ng!Passw0rd"),
                    credentialRepository.credentials[LibraryUserId(it.libraryUserId)]
                )
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
            passwordHasher = PasswordHasher { hashed("hashed-$it") },
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
            passwordHasher = PasswordHasher { hashed("hashed-$it") },
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

        override fun existsByEmail(email: Email): EmailExistence = EmailExistence(existsByEmail)
    }

    private class InMemoryAuthCredentialRepository : AuthCredentialRepository {
        val credentials = mutableMapOf<LibraryUserId, PasswordHash>()

        override fun save(credential: AuthCredential) {
            credentials[credential.libraryUserId] = credential.passwordHash
        }

        override fun findByLibraryUserId(libraryUserId: LibraryUserId) = null
    }
}
