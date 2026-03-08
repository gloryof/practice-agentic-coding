package jp.glory.practice.agentic.libraryuser.command.domain.event

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegisteredEventTest {
    @Test
    fun `keeps domain model values`() {
        val email =
            Email.create("user@example.com").fold(
                success = { it },
                failure = { error("expected success") },
            )
        val occurredAt = Instant.parse("2026-02-22T12:34:56Z")

        val event =
            LibraryUserRegisteredEvent(
                libraryUserId = LibraryUserId("user-id-001"),
                email = email,
                occurredAt = occurredAt,
            )

        assertEquals(LibraryUserId("user-id-001"), event.libraryUserId)
        assertEquals(email, event.email)
        assertEquals(occurredAt, event.occurredAt)
    }
}
