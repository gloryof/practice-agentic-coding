package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.jdbc.JdbcDatabase
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LibraryUserCommandRepositoryImplTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: LibraryUserCommandRepositoryImpl

    @Autowired
    private lateinit var database: JdbcDatabase

    private val table = Meta.libraryUserTable.clone(table = "library_users")

    @Test
    fun `save stores record in library_users`() {
        val event =
            LibraryUserRegisteredEvent(
                libraryUserId = LibraryUserId("user0000000000000000000001"),
                email = email("user1@example.com"),
                occurredAt = Instant.parse("2026-03-08T00:00:00Z"),
            )

        sut.save(event)

        val stored =
            database.runQuery {
                QueryDsl
                    .from(table)
                    .where { table.id eq event.libraryUserId.value }
                    .firstOrNull()
            }
        assertNotNull(stored)
        assertEquals(event.libraryUserId.value, stored.id)
        assertEquals(event.email.value, stored.email)
        assertEquals(event.occurredAt, stored.registeredAt)
    }

    @Test
    fun `existsByEmail returns true when email exists`() {
        val event =
            LibraryUserRegisteredEvent(
                libraryUserId = LibraryUserId("user0000000000000000000002"),
                email = email("exists@example.com"),
                occurredAt = Instant.parse("2026-03-08T00:00:00Z"),
            )
        sut.save(event)

        val result = sut.existsByEmail(email("exists@example.com"))

        assertTrue(result.value)
    }

    @Test
    fun `existsByEmail returns false when email does not exist`() {
        val result = sut.existsByEmail(email("none@example.com"))

        assertFalse(result.value)
    }

    private fun email(raw: String): Email =
        Email.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}
