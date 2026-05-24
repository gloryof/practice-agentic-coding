package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthAccountRepositoryImplTest {
    @Nested
    inner class FindByEmail {
        @Test
        fun `given existing email when find by email then returns account`() {
            val dao = mockk<LibraryUserDao>()
            val sut = AuthAccountRepositoryImpl(dao)
            every { dao.findByEmail("user11@example.com") } returns
                LibraryUserTable(
                    id = "user0000000000000000000011",
                    email = "user11@example.com",
                    registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
                )

            val account = sut.findByEmail(email("user11@example.com"))

            assertNotNull(account)
            assertEquals("user0000000000000000000011", account.libraryUserId.value)
            assertEquals("user11@example.com", account.email.value)
        }

        @Test
        fun `given missing email when find by email then returns null`() {
            val dao = mockk<LibraryUserDao>()
            val sut = AuthAccountRepositoryImpl(dao)
            every { dao.findByEmail("none@example.com") } returns null

            val account = sut.findByEmail(email("none@example.com"))

            assertNull(account)
        }

        @Test
        fun `given invalid stored email when find by email then throws exception`() {
            val dao = mockk<LibraryUserDao>()
            val sut = AuthAccountRepositoryImpl(dao)
            every { dao.findByEmail("bad@example.com") } returns
                LibraryUserTable(
                    id = "user0000000000000000000012",
                    email = "",
                    registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
                )

            assertFailsWith<IllegalStateException> {
                sut.findByEmail(email("bad@example.com"))
            }
        }
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
