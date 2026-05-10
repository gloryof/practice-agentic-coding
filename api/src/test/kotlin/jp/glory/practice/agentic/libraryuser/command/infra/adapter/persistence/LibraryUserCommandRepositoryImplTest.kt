package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibraryUserCommandRepositoryImplTest {
    @Test
    fun `save delegates insert to dao`() {
        val dao = mockk<LibraryUserDao>(relaxed = true)
        val sut = LibraryUserCommandRepositoryImpl(dao)
        val captured = slot<LibraryUserTable>()
        val event =
            LibraryUserRegisteredEvent(
                libraryUserId = LibraryUserId("user0000000000000000000001"),
                email = email("user1@example.com"),
                occurredAt = Instant.parse("2026-03-08T00:00:00Z"),
            )

        every { dao.insert(capture(captured)) } returns Unit

        sut.save(event)

        verify(exactly = 1) { dao.insert(any()) }
        assertEquals(event.libraryUserId.value, captured.captured.id)
        assertEquals(event.email.value, captured.captured.email)
        assertEquals(event.occurredAt, captured.captured.registeredAt)
    }

    @Test
    fun `existsByEmail returns true when email exists`() {
        val dao = mockk<LibraryUserDao>()
        val sut = LibraryUserCommandRepositoryImpl(dao)

        every { dao.existsByEmail("exists@example.com") } returns true

        val result = sut.existsByEmail(email("exists@example.com"))

        assertTrue(result.value)
    }

    @Test
    fun `existsByEmail returns false when email does not exist`() {
        val dao = mockk<LibraryUserDao>()
        val sut = LibraryUserCommandRepositoryImpl(dao)

        every { dao.existsByEmail("none@example.com") } returns false

        val result = sut.existsByEmail(email("none@example.com"))

        assertFalse(result.value)
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
