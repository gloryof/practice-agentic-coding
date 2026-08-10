package jp.glory.practice.agentic.auth.command.infra.adapter.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEvent
import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.auth.AccessTokenStore
import java.time.Instant
import kotlin.test.Test

class AuthLoggedOutEventHandlerImplTest {
    @Test
    fun `given logged out event when handle then removes current access token`() {
        val accessTokenStore = mockk<AccessTokenStore>()
        every { accessTokenStore.remove("token-123") } returns Unit
        val sut = AuthLoggedOutEventHandlerImpl(accessTokenStore)

        sut.handle(
            AuthLoggedOutEvent(
                libraryUserId = LibraryUserId("user-id"),
                accessToken = "token-123",
                occurredAt = Instant.parse("2026-02-22T12:34:56Z"),
            ),
        )

        verify(exactly = 1) { accessTokenStore.remove("token-123") }
    }
}
