package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.libraryUserTable
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.core.dsl.query.single
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class LibraryUserDao(
    private val database: JdbcDatabase,
) {
    private val table = Meta.libraryUserTable

    fun insert(record: LibraryUserTable) {
        database.runQuery {
            QueryDsl.insert(table).single(record)
        }
    }

    fun findByEmail(email: String): LibraryUserTable? =
        database.runQuery {
            QueryDsl
                .from(table)
                .where { table.email eq email }
                .firstOrNull()
        }

    fun existsByEmail(email: String): Boolean = findByEmail(email) != null
}
