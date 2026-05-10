package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.AuthCredentialTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authCredentialTable
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.core.dsl.query.single
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class AuthCredentialDao(
    private val database: JdbcDatabase,
) {
    private val table = Meta.authCredentialTable.clone(table = "auth_credentials")

    fun insert(record: AuthCredentialTable) {
        database.runQuery {
            QueryDsl.insert(table).single(record)
        }
    }

    fun findByLibraryUserId(libraryUserId: String): AuthCredentialTable? =
        database.runQuery {
            QueryDsl
                .from(table)
                .where { table.libraryUserId eq libraryUserId }
                .firstOrNull()
        }
}
