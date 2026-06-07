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
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEventHandler
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.shared.domain.DomainError
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.junit.jupiter.api.Nested
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterLibraryUserUseCaseTest {
    private data class TestContext(
        val sut: RegisterLibraryUserUseCase,
        val registrationService: LibraryUserRegistrationService,
        val libraryUserRegisteredEventHandler: LibraryUserRegisteredEventHandler,
    )

    @Nested
    inner class Register {
        @Test
        fun `given valid input when register then handles registered event`() {
            val email = Email.create("user@example.com").getOrThrow { throw IllegalArgumentException("invalid param") }
            val input = RegisterLibraryUserInput(email.value, "Str0ng!Passw0rd")

            val eventSlot = slot<LibraryUserRegisteredEvent>()

            val context =
                createSut(
                    clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
                )
            every { context.registrationService.verify(email) } returns Ok(Unit)
            every { context.libraryUserRegisteredEventHandler.handle(any()) } just Runs

            val result = context.sut.register(input)
            result.fold(
                success = {
                    assertEquals("user@example.com", it.email)
                    assertEquals("LibraryUserRegisteredEvent", it.eventName)
                },
                failure = { error("expected success") },
            )

            verify(exactly = 1) { context.libraryUserRegisteredEventHandler.handle(capture(eventSlot)) }
            assertEquals("user@example.com", eventSlot.captured.email.value)
            assertEquals("Str0ng!Passw0rd", eventSlot.captured.rawPassword.value)
        }

        @Test
        fun `given duplicate email when register then returns duplicate email error`() {
            val context = createSut()
            every { context.registrationService.verify(any()) } returns Err(DomainError.DuplicateEmail)

            val result = context.sut.register(RegisterLibraryUserInput("user@example.com", "Str0ng!Passw0rd"))
            assertEquals(Err(UsecaseError.DuplicateEmail), result)
            verify(exactly = 0) { context.libraryUserRegisteredEventHandler.handle(any()) }
        }

        @Test
        fun `given invalid password when register then returns validation error`() {
            val context = createSut()

            val result = context.sut.register(RegisterLibraryUserInput("user@example.com", "short"))
            assertEquals(
                Err(UsecaseError.Validation(field = "password", reason = "must_meet_password_policy")),
                result,
            )
            verify(exactly = 0) { context.registrationService.verify(any()) }
            verify(exactly = 0) { context.libraryUserRegisteredEventHandler.handle(any()) }
        }
    }

    private fun createSut(
        registrationService: LibraryUserRegistrationService = mockk(),
        libraryUserRegisteredEventHandler: LibraryUserRegisteredEventHandler = mockk(),
        clock: Clock = Clock.systemDefaultZone(),
    ): TestContext {
        val sut =
            RegisterLibraryUserUseCase(
                registrationService = registrationService,
                libraryUserRegisteredEventHandler = libraryUserRegisteredEventHandler,
                clock = clock,
            )
        return TestContext(
            sut = sut,
            registrationService = registrationService,
            libraryUserRegisteredEventHandler = libraryUserRegisteredEventHandler,
        )
    }
}
