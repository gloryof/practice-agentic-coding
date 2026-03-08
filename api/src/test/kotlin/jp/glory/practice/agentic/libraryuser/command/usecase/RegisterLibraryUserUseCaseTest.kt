package jp.glory.practice.agentic.libraryuser.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class RegisterLibraryUserUseCaseTest {
    private val userRepository = mockk<LibraryUserCommandRepository>()
    private val credentialRepository = mockk<AuthCredentialRepository>()
    private val passwordHasher = PasswordHasher { hashed("hashed-$it") }

    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )

    @Test
    fun `registers user and credential`() {
        every { userRepository.existsByEmail(any()) } returns EmailExistence(false)
        every { userRepository.save(any()) } just Runs
        every { credentialRepository.save(any()) } just Runs

        val eventSlot = slot<LibraryUserRegisteredEvent>()
        val credentialSlot = slot<AuthCredential>()

        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = credentialRepository,
            passwordHasher = passwordHasher,
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        result.fold(
            success = {
                assertEquals("user@example.com", it.email)
                assertEquals("LibraryUserRegisteredEvent", it.eventName)
            },
            failure = { error("expected success") },
        )

        verify(exactly = 1) { userRepository.save(capture(eventSlot)) }
        verify(exactly = 1) { credentialRepository.save(capture(credentialSlot)) }
        assertEquals("user@example.com", eventSlot.captured.email.value)
        assertEquals(eventSlot.captured.libraryUserId, credentialSlot.captured.libraryUserId)
        assertEquals(hashed("hashed-Str0ng!Passw0rd"), credentialSlot.captured.passwordHash)
    }

    @Test
    fun `returns err on duplicate email`() {
        every { userRepository.existsByEmail(any()) } returns EmailExistence(true)
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = credentialRepository,
            passwordHasher = passwordHasher,
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        assertEquals(Err(UsecaseError.DuplicateEmail), result)
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { credentialRepository.save(any()) }
    }

    @Test
    fun `returns validation error on invalid password`() {
        val useCase = RegisterLibraryUserUseCase(
            registrationService = LibraryUserRegistrationService(userRepository),
            libraryUserRepository = userRepository,
            authCredentialRepository = credentialRepository,
            passwordHasher = passwordHasher,
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

        val result = useCase.register(RegisterLibraryUserInput("user@example.com", "short"))
        assertEquals(
            Err(UsecaseError.Validation(field = "password", reason = "must_meet_password_policy")),
            result
        )
        verify(exactly = 0) { userRepository.existsByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { credentialRepository.save(any()) }
    }
}
