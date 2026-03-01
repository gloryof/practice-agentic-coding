package jp.glory.practice.agentic.libraryuser.command.infra

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class AuthAccountRepositoryImpl(
    private val database: JdbcDatabase,
) : AuthAccountRepository {
    private val table = Meta.libraryUserTable.clone(table = "library_users")

    override fun findByEmail(email: String): AuthAccount? {
        val user = database.runQuery {
            QueryDsl.from(table)
                .where { table.email eq email }
                .firstOrNull()
        } ?: return null

        return AuthAccount(
            libraryUserId = user.id,
            email = user.email,
        )
    }
}
