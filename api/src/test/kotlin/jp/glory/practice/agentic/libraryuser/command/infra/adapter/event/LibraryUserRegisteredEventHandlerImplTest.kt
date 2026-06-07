package jp.glory.practice.agentic.libraryuser.command.infra.adapter.event

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
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.model.RawPassword
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import org.junit.jupiter.api.Nested
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegisteredEventHandlerImplTest {
    @Nested
    inner class Handle {
        @Test
        fun `given registered event when handle then saves user and credential`() {
            val repository = mockk<LibraryUserCommandRepository>()
            val authCredentialRepository = mockk<AuthCredentialRepository>()
            val sut =
                LibraryUserRegisteredEventHandlerImpl(
                    libraryUserRepository = repository,
                    authCredentialRepository = authCredentialRepository,
                    passwordHasher = PasswordHasher { hashed("hashed-$it") },
                )
            val libraryUserSlot = slot<LibraryUser>()
            val registeredAtSlot = slot<Instant>()
            val credentialSlot = slot<AuthCredential>()
            val occurredAt = Instant.parse("2026-03-08T00:00:00Z")
            val event =
                LibraryUserRegisteredEvent(
                    libraryUserId = LibraryUserId("user0000000000000000000001"),
                    email = email("user1@example.com"),
                    rawPassword = rawPassword("Str0ng!Passw0rd"),
                    occurredAt = occurredAt,
                )
            every { repository.save(capture(libraryUserSlot), capture(registeredAtSlot)) } returns Unit
            every { authCredentialRepository.save(capture(credentialSlot)) } just Runs

            sut.handle(event)

            verify(exactly = 1) { repository.save(any(), any()) }
            verify(exactly = 1) { authCredentialRepository.save(any()) }
            assertEquals(event.libraryUserId, libraryUserSlot.captured.id)
            assertEquals(event.email, libraryUserSlot.captured.email)
            assertEquals(occurredAt, registeredAtSlot.captured)
            assertEquals(event.libraryUserId, credentialSlot.captured.libraryUserId)
            assertEquals(hashed("hashed-Str0ng!Passw0rd"), credentialSlot.captured.passwordHash)
        }
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )

    private fun rawPassword(raw: String): RawPassword =
        RawPassword.create(raw).fold(
            success = { it },
            failure = { error("expected valid raw password") },
        )

    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )
}
