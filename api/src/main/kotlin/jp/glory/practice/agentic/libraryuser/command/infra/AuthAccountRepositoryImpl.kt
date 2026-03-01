package jp.glory.practice.agentic.libraryuser.command.infra

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
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

    override fun findByEmail(email: Email): AuthAccount? {
        val user = database.runQuery {
            QueryDsl.from(table)
                .where { table.email eq email.value }
                .firstOrNull()
        } ?: return null

        val storedEmail = Email.create(user.email).fold(
            success = { it },
            failure = { throw IllegalStateException("Invalid email stored in library_users: ${user.email}") }
        )

        return AuthAccount(
            libraryUserId = LibraryUserId(user.id),
            email = storedEmail,
        )
    }
}
