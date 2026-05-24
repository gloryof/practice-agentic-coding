package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LibraryUserDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: LibraryUserDao

    @Nested
    inner class Queries {
        @Test
        fun `given inserted user when find by email then returns record`() {
            val record =
                LibraryUserTable(
                    id = "user0000000000000000000101",
                    email = "dao-user@example.com",
                    registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
                )

            sut.insert(record)
            val found = sut.findByEmail(record.email)

            assertNotNull(found)
            assertEquals(record.id, found.id)
            assertEquals(record.email, found.email)
            assertEquals(record.registeredAt, found.registeredAt)
        }

        @Test
        fun `given missing email when find by email then returns null`() {
            assertNull(sut.findByEmail("missing@example.com"))
        }

        @Test
        fun `given existing email when exists by email then returns true`() {
            sut.insert(
                LibraryUserTable(
                    id = "user0000000000000000000102",
                    email = "exists@example.com",
                    registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
                ),
            )

            assertTrue(sut.existsByEmail("exists@example.com"))
        }
    }
}
