package jp.glory.practice.agentic.auth.command.infra

import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.core.dsl.query.single
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class AuthCredentialRepositoryImpl(
    private val database: JdbcDatabase,
) : AuthCredentialRepository {
    private val table = Meta.authCredentialTable.clone(table = "auth_credentials")

    override fun save(libraryUserId: String, passwordHash: String) {
        database.runQuery {
            QueryDsl.insert(table).single(
                AuthCredentialTable(
                    libraryUserId = libraryUserId,
                    passwordHash = passwordHash,
                )
            )
        }
    }

    override fun findByLibraryUserId(libraryUserId: String): AuthCredential? {
        val credential = database.runQuery {
            QueryDsl.from(table)
                .where { table.libraryUserId eq libraryUserId }
                .firstOrNull()
        } ?: return null

        return AuthCredential(
            libraryUserId = credential.libraryUserId,
            passwordHash = credential.passwordHash,
        )
    }
}
