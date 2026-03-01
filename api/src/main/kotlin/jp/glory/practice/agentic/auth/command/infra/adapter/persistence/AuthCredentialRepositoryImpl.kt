package jp.glory.practice.agentic.auth.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
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

    override fun save(credential: AuthCredential) {
        database.runQuery {
            QueryDsl.insert(table).single(
                AuthCredentialTable(
                    libraryUserId = credential.libraryUserId.value,
                    passwordHash = credential.passwordHash.value,
                )
            )
        }
    }

    override fun findByLibraryUserId(libraryUserId: LibraryUserId): AuthCredential? {
        val credential = database.runQuery {
            QueryDsl.from(table)
                .where { table.libraryUserId eq libraryUserId.value }
                .firstOrNull()
        } ?: return null

        return AuthCredential(
            libraryUserId = LibraryUserId(credential.libraryUserId),
            passwordHash = PasswordHash.create(credential.passwordHash).fold(
                success = { it },
                failure = { throw IllegalStateException("Invalid password hash stored in auth_credentials") }
            ),
        )
    }
}
