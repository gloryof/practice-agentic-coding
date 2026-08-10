package jp.glory.practice.agentic.auth.command.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEvent
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEventHandler
import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import org.junit.jupiter.api.Nested
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LogoutUseCaseTest {
    @Nested
    inner class Logout {
        @Test
        fun `given authenticated session when logout then publishes logged out event`() {
            val handler = mockk<AuthLoggedOutEventHandler>()
            val eventSlot = slot<AuthLoggedOutEvent>()
            every { handler.handle(capture(eventSlot)) } returns Unit
            val sut = createSut(handler)

            sut.logout(input())

            verify(exactly = 1) { handler.handle(any()) }
            assertEquals(LibraryUserId("user-id"), eventSlot.captured.libraryUserId)
            assertEquals("token-123", eventSlot.captured.accessToken)
            assertEquals(Instant.parse("2026-02-22T12:34:56Z"), eventSlot.captured.occurredAt)
        }

        @Test
        fun `given session store failure when logout then propagates technical failure`() {
            val handler = mockk<AuthLoggedOutEventHandler>()
            every { handler.handle(any()) } throws IllegalStateException("store unavailable")
            val sut = createSut(handler)

            assertFailsWith<IllegalStateException> {
                sut.logout(input())
            }

            verify(exactly = 1) { handler.handle(any()) }
        }
    }

    private fun createSut(handler: AuthLoggedOutEventHandler): LogoutUseCase =
        LogoutUseCase(
            authLoggedOutEventHandler = handler,
            clock = Clock.fixed(Instant.parse("2026-02-22T12:34:56Z"), ZoneOffset.UTC),
        )

    private fun input(): LogoutInput =
        LogoutInput(
            libraryUserId = "user-id",
            accessToken = "token-123",
        )
}
