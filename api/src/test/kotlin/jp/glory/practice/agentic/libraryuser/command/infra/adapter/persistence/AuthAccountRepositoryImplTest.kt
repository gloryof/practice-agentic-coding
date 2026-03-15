package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.jdbc.JdbcDatabase
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthAccountRepositoryImplTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: AuthAccountRepositoryImpl

    @Autowired
    private lateinit var database: JdbcDatabase

    private val table = Meta.libraryUserTable.clone(table = "library_users")

    @Test
    fun `findByEmail returns account when email exists`() {
        database.runQuery {
            QueryDsl.Companion.insert(table).single(
                LibraryUserTable(
                    id = "user0000000000000000000011",
                    email = "user11@example.com",
                    registeredAt = Instant.parse("2026-03-08T00:00:00Z"),
                ),
            )
        }

        val account = sut.findByEmail(email("user11@example.com"))

        assertNotNull(account)
        assertEquals("user0000000000000000000011", account.libraryUserId.value)
        assertEquals("user11@example.com", account.email.value)
    }

    @Test
    fun `findByEmail returns null when email does not exist`() {
        val account = sut.findByEmail(email("none@example.com"))

        assertNull(account)
    }

    private fun email(raw: String): Email =
        Email.Companion.create(raw).fold(
            success = { it },
            failure = { error("expected valid email") },
        )
}