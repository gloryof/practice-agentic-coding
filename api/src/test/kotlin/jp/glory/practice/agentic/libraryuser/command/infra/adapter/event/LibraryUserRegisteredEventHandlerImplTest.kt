package jp.glory.practice.agentic.libraryuser.command.infra.adapter.event

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import org.junit.jupiter.api.Nested
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegisteredEventHandlerImplTest {
    @Nested
    inner class Handle {
        @Test
        fun `given registered event when handle then saves user`() {
            val repository = mockk<LibraryUserCommandRepository>()
            val sut =
                LibraryUserRegisteredEventHandlerImpl(
                    libraryUserRepository = repository,
                )
            val libraryUserSlot = slot<LibraryUser>()
            val registeredAtSlot = slot<Instant>()
            val occurredAt = Instant.parse("2026-03-08T00:00:00Z")
            val event =
                LibraryUserRegisteredEvent(
                    libraryUserId = LibraryUserId("user0000000000000000000001"),
                    email = email("user1@example.com"),
                    occurredAt = occurredAt,
                )
            every { repository.save(capture(libraryUserSlot), capture(registeredAtSlot)) } returns Unit

            sut.handle(event)

            verify(exactly = 1) { repository.save(any(), any()) }
            assertEquals(event.libraryUserId, libraryUserSlot.captured.id)
            assertEquals(event.email, libraryUserSlot.captured.email)
            assertEquals(occurredAt, registeredAtSlot.captured)
        }
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
