package jp.glory.practice.agentic.auth.command.infra.adapter.event

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedInEvent
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.auth.AccessTokenSession
import jp.glory.practice.agentic.shared.auth.AccessTokenStore
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthLoggedInEventHandlerImplTest {
    @Test
    fun `given logged in event when handle then saves access token session`() {
        val accessTokenStore = mockk<AccessTokenStore>()
        val sut = AuthLoggedInEventHandlerImpl(accessTokenStore)
        val sessionSlot = slot<AccessTokenSession>()
        val account = AuthAccount(LibraryUserId("user-id"), email("user@example.com"))
        val expiresAt = Instant.parse("2026-02-23T12:34:56Z")
        val event =
            AuthLoggedInEvent(
                account = account,
                accessToken = "token-123",
                expiresAt = expiresAt,
                occurredAt = Instant.parse("2026-02-22T12:34:56Z"),
            )
        every { accessTokenStore.save(capture(sessionSlot)) } returns Unit

        sut.handle(event)

        verify(exactly = 1) { accessTokenStore.save(any()) }
        assertEquals("token-123", sessionSlot.captured.token)
        assertEquals(account.libraryUserId, sessionSlot.captured.libraryUserId)
        assertEquals(expiresAt, sessionSlot.captured.expiresAt)
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
