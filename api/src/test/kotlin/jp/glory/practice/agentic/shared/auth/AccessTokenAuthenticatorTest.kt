package jp.glory.practice.agentic.shared.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.web.LoginRequiredApiException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AccessTokenAuthenticatorTest {
    private val now = Instant.parse("2026-02-22T12:34:56Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    @Test
    fun `allows request when token is valid`() {
        val store = mockk<AccessTokenStore>()
        val session =
            AccessTokenSession(
                token = "token-123",
                libraryUserId = LibraryUserId("user-id"),
                expiresAt = now.plusSeconds(60),
            )
        every { store.find("token-123") } returns session

        val sut = AccessTokenAuthenticator(store, clock)

        sut.requireValidToken("Bearer token-123")

        verify(exactly = 0) { store.remove(any()) }
    }

    @Test
    fun `rejects request when authorization header is missing`() {
        val store = mockk<AccessTokenStore>(relaxed = true)
        val sut = AccessTokenAuthenticator(store, clock)

        assertFailsWith<LoginRequiredApiException> {
            sut.requireValidToken(null)
        }

        verify(exactly = 0) { store.find(any()) }
    }

    @Test
    fun `rejects request when token is expired`() {
        val store = mockk<AccessTokenStore>()
        val session =
            AccessTokenSession(
                token = "token-123",
                libraryUserId = LibraryUserId("user-id"),
                expiresAt = now.minusSeconds(1),
            )
        every { store.find("token-123") } returns session
        every { store.remove("token-123") } returns Unit

        val sut = AccessTokenAuthenticator(store, clock)

        assertFailsWith<LoginRequiredApiException> {
            sut.requireValidToken("Bearer token-123")
        }

        verify(exactly = 1) { store.remove("token-123") }
    }
}
