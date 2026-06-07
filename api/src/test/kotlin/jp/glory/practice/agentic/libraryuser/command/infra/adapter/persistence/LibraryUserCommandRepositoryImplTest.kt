package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibraryUserCommandRepositoryImplTest {
    @Nested
    inner class Save {
        @Test
        fun `given library user when save then delegates insert to dao`() {
            val dao = mockk<LibraryUserDao>(relaxed = true)
            val sut = LibraryUserCommandRepositoryImpl(dao)
            val captured = slot<LibraryUserTable>()
            val libraryUser =
                LibraryUser(
                    id = LibraryUserId("user0000000000000000000001"),
                    email = email("user1@example.com"),
                )
            val registeredAt = Instant.parse("2026-03-08T00:00:00Z")

            every { dao.insert(capture(captured)) } returns Unit

            sut.save(libraryUser, registeredAt)

            verify(exactly = 1) { dao.insert(any()) }
            assertEquals(libraryUser.id.value, captured.captured.id)
            assertEquals(libraryUser.email.value, captured.captured.email)
            assertEquals(registeredAt, captured.captured.registeredAt)
        }
    }

    @Nested
    inner class ExistsByEmail {
        @Test
        fun `given existing email when exists by email then returns true`() {
            val dao = mockk<LibraryUserDao>()
            val sut = LibraryUserCommandRepositoryImpl(dao)

            every { dao.existsByEmail("exists@example.com") } returns true

            val result = sut.existsByEmail(email("exists@example.com"))

            assertTrue(result.value)
        }

        @Test
        fun `given missing email when exists by email then returns false`() {
            val dao = mockk<LibraryUserDao>()
            val sut = LibraryUserCommandRepositoryImpl(dao)

            every { dao.existsByEmail("none@example.com") } returns false

            val result = sut.existsByEmail(email("none@example.com"))

            assertFalse(result.value)
        }
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
