package jp.glory.practice.agentic.libraryuser.command.domain.event

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.model.RawPassword
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegisteredEventTest {
    @Test
    fun `given constructor values when create event then keeps domain model values`() {
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
                rawPassword = rawPassword("Str0ng!Passw0rd"),
                occurredAt = occurredAt,
            )

        assertEquals(LibraryUserId("user-id-001"), event.libraryUserId)
        assertEquals(email, event.email)
        assertEquals(rawPassword("Str0ng!Passw0rd"), event.rawPassword)
        assertEquals(occurredAt, event.occurredAt)
    }

    @Test
    fun `given event when convert to library user then creates model from event values`() {
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
                rawPassword = rawPassword("Str0ng!Passw0rd"),
                occurredAt = occurredAt,
            )

        val libraryUser = event.toLibraryUser()

        assertEquals(LibraryUserId("user-id-001"), libraryUser.id)
        assertEquals(email, libraryUser.email)
    }

    private fun rawPassword(raw: String): RawPassword =
        RawPassword.create(raw).fold(
            success = { it },
            failure = { error("expected success") },
        )
}
