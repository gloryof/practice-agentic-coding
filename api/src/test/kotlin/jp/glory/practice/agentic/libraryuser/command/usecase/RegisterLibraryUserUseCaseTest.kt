package jp.glory.practice.agentic.libraryuser.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrThrow
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
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.shared.domain.DomainError
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterLibraryUserUseCaseTest {
    private data class TestContext(
        val sut: RegisterLibraryUserUseCase,
        val registrationService: LibraryUserRegistrationService,
        val libraryUserRepository: LibraryUserCommandRepository,
        val authCredentialRepository: AuthCredentialRepository,
    )

    @Test
    fun `registers user and credential`() {
        val email = Email.create("user@example.com").getOrThrow { throw IllegalArgumentException("invalid param") }
        val input = RegisterLibraryUserInput(email.value, "Str0ng!Passw0rd")

        val eventSlot = slot<LibraryUserRegisteredEvent>()
        val credentialSlot = slot<AuthCredential>()

        val context =
            createSut(
                passwordHasher = PasswordHasher { hashed("hashed-$it") },
                clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
            )
        every { context.registrationService.verify(email) } returns Ok(Unit)
        every { context.libraryUserRepository.save(any()) } just Runs
        every { context.authCredentialRepository.save(any()) } just Runs

        val result = context.sut.register(input)
        result.fold(
            success = {
                assertEquals("user@example.com", it.email)
                assertEquals("LibraryUserRegisteredEvent", it.eventName)
            },
            failure = { error("expected success") },
        )

        verify(exactly = 1) { context.libraryUserRepository.save(capture(eventSlot)) }
        verify(exactly = 1) { context.authCredentialRepository.save(capture(credentialSlot)) }
        assertEquals("user@example.com", eventSlot.captured.email.value)
        assertEquals(eventSlot.captured.libraryUserId, credentialSlot.captured.libraryUserId)
        assertEquals(hashed("hashed-Str0ng!Passw0rd"), credentialSlot.captured.passwordHash)
    }

    @Test
    fun `returns err on duplicate email`() {
        val context = createSut()
        every { context.registrationService.verify(any()) } returns Err(DomainError.DuplicateEmail)

        val result = context.sut.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
        assertEquals(Err(UsecaseError.DuplicateEmail), result)
        verify(exactly = 0) { context.libraryUserRepository.save(any()) }
        verify(exactly = 0) { context.authCredentialRepository.save(any()) }
    }

    @Test
    fun `returns validation error on invalid password`() {
        val context = createSut()

        val result = context.sut.register(RegisterLibraryUserInput("user@example.com", "short"))
        assertEquals(
            Err(UsecaseError.Validation(field = "password", reason = "must_meet_password_policy")),
            result,
        )
        verify(exactly = 0) { context.libraryUserRepository.existsByEmail(any()) }
        verify(exactly = 0) { context.libraryUserRepository.save(any()) }
        verify(exactly = 0) { context.authCredentialRepository.save(any()) }
    }

    private fun createSut(
        registrationService: LibraryUserRegistrationService = mockk(),
        libraryUserRepository: LibraryUserCommandRepository = mockk(),
        authCredentialRepository: AuthCredentialRepository = mockk(),
        passwordHasher: PasswordHasher = mockk(),
        clock: Clock = Clock.systemDefaultZone(),
    ): TestContext {
        val sut =
            RegisterLibraryUserUseCase(
                registrationService = registrationService,
                libraryUserRepository = libraryUserRepository,
                authCredentialRepository = authCredentialRepository,
                passwordHasher = passwordHasher,
                clock = clock,
            )
        return TestContext(
            sut = sut,
            registrationService = registrationService,
            libraryUserRepository = libraryUserRepository,
            authCredentialRepository = authCredentialRepository,
        )
    }

    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )
}
