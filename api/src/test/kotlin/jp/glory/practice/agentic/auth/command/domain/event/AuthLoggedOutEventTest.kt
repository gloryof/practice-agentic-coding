package jp.glory.practice.agentic.auth.command.domain.event

import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthLoggedOutEventTest {
    @Test
    fun `given constructor values when create event then keeps logout values`() {
        val occurredAt = Instant.parse("2026-02-22T12:34:56Z")

        val event =
            AuthLoggedOutEvent(
                libraryUserId = LibraryUserId("user-id"),
                accessToken = "token-123",
                occurredAt = occurredAt,
            )

        assertEquals(LibraryUserId("user-id"), event.libraryUserId)
        assertEquals("token-123", event.accessToken)
        assertEquals(occurredAt, event.occurredAt)
    }
}
