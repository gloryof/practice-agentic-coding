package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.AuthCredentialTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthCredentialDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: AuthCredentialDao

    @Autowired
    private lateinit var libraryUserDao: LibraryUserDao

    @Test
    fun `insert and findByLibraryUserId`() {
        libraryUserDao.insert(
            LibraryUserTable(
                id = "user0000000000000000000201",
                email = "credential@example.com",
                registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
            ),
        )
        val record =
            AuthCredentialTable(
                libraryUserId = "user0000000000000000000201",
                passwordHash = "hashed-value-201",
            )

        sut.insert(record)
        val found = sut.findByLibraryUserId(record.libraryUserId)

        assertNotNull(found)
        assertEquals(record.libraryUserId, found.libraryUserId)
        assertEquals(record.passwordHash, found.passwordHash)
    }

    @Test
    fun `findByLibraryUserId returns null when not found`() {
        assertNull(sut.findByLibraryUserId("user0000000000000000000299"))
    }
}
