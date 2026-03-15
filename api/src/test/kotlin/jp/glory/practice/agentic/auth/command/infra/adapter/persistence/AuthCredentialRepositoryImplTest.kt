package jp.glory.practice.agentic.auth.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.core.dsl.query.single
import org.komapper.jdbc.JdbcDatabase
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthCredentialRepositoryImplTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: AuthCredentialRepositoryImpl

    @Autowired
    private lateinit var database: JdbcDatabase

    private val credentialTable = Meta.authCredentialTable.clone(table = "auth_credentials")

    @Test
    fun `save stores record in auth_credentials`() {
        val userId = LibraryUserId("user0000000000000000000021")
        insertLibraryUser(userId)

        sut.save(
            AuthCredential(
                libraryUserId = userId,
                passwordHash = passwordHash("hashed-value-21"),
            ),
        )

        val stored =
            database.runQuery {
                QueryDsl
                    .from(credentialTable)
                    .where { credentialTable.libraryUserId eq userId.value }
                    .firstOrNull()
            }
        assertNotNull(stored)
        assertEquals(userId.value, stored.libraryUserId)
        assertEquals("hashed-value-21", stored.passwordHash)
    }

    @Test
    fun `findByLibraryUserId returns credential when record exists`() {
        val userId = LibraryUserId("user0000000000000000000022")
        insertLibraryUser(userId)
        database.runQuery {
            QueryDsl.insert(credentialTable).single(
                AuthCredentialTable(
                    libraryUserId = userId.value,
                    passwordHash = "hashed-value-22",
                ),
            )
        }

        val credential = sut.findByLibraryUserId(userId)

        assertNotNull(credential)
        assertEquals(userId.value, credential.libraryUserId.value)
        assertEquals("hashed-value-22", credential.passwordHash.value)
    }

    @Test
    fun `findByLibraryUserId returns null when record does not exist`() {
        val credential = sut.findByLibraryUserId(LibraryUserId("user0000000000000000000023"))

        assertNull(credential)
    }

    private fun insertLibraryUser(userId: LibraryUserId) {
        jdbcTemplate.update(
            "INSERT INTO library_users (id, email, registered_at) VALUES (?, ?, ?::timestamptz)",
            userId.value,
            "${userId.value}@example.com",
            Instant.parse("2026-03-08T00:00:00Z").toString(),
        )
    }

    private fun passwordHash(raw: String): PasswordHash =
        PasswordHash.create(raw).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )
}
