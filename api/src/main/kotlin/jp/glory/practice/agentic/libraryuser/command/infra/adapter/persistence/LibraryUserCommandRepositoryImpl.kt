package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.core.dsl.query.single
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class LibraryUserCommandRepositoryImpl(
    private val database: JdbcDatabase,
) : LibraryUserCommandRepository {
    private val table = Meta.libraryUserTable.clone(table = "library_users")

    override fun save(event: LibraryUserRegisteredEvent) {
        database.runQuery {
            QueryDsl.insert(table).single(
                LibraryUserTable(
                    id = event.libraryUserId.value,
                    email = event.email.value,
                    registeredAt = event.occurredAt,
                )
            )
        }
    }

    override fun existsByEmail(email: Email): EmailExistence {
        val exists = database.runQuery {
            QueryDsl.from(table)
                .where { table.email eq email.value }
                .firstOrNull()
        } != null
        return EmailExistence(exists)
    }
}
