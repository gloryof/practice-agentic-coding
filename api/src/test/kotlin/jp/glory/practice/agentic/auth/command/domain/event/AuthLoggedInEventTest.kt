package jp.glory.practice.agentic.auth.command.domain.event

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.model.Email
import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import org.junit.jupiter.api.Nested
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthLoggedInEventTest {
    @Nested
    inner class Create {
        @Test
        fun `given constructor values when create event then keeps domain model values`() {
            val account = AuthAccount(LibraryUserId("user-id"), email("user@example.com"))
            val occurredAt = Instant.parse("2026-02-22T12:34:56Z")
            val expiresAt = Instant.parse("2026-02-23T12:34:56Z")

            val event =
                AuthLoggedInEvent(
                    account = account,
                    accessToken = "token-123",
                    expiresAt = expiresAt,
                    occurredAt = occurredAt,
                )

            assertEquals(account, event.account)
            assertEquals("token-123", event.accessToken)
            assertEquals(expiresAt, event.expiresAt)
            assertEquals(occurredAt, event.occurredAt)
        }
    }

    @Nested
    inner class ToAccessTokenSession {
        @Test
        fun `given event when convert to access token session then creates session from event values`() {
            val account = AuthAccount(LibraryUserId("user-id"), email("user@example.com"))
            val expiresAt = Instant.parse("2026-02-23T12:34:56Z")
            val event =
                AuthLoggedInEvent(
                    account = account,
                    accessToken = "token-123",
                    expiresAt = expiresAt,
                    occurredAt = Instant.parse("2026-02-22T12:34:56Z"),
                )

            val session = event.toAccessTokenSession()

            assertEquals("token-123", session.token)
            assertEquals(account.libraryUserId.value, session.libraryUserId)
            assertEquals(expiresAt, session.expiresAt)
        }
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
