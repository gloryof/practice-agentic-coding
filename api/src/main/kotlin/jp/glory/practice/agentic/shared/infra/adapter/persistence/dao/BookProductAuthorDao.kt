package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductAuthorTable
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class BookProductAuthorDao(
    private val database: JdbcDatabase,
) {
    private val authors = Meta.authorTable.clone(table = "authors")
    private val bookProductAuthors = Meta.bookProductAuthorTable.clone(table = "book_product_authors")

    fun findAuthorNamesByBookProductIds(bookProductIds: List<String>): Map<String, List<String>> {
        if (bookProductIds.isEmpty()) {
            return emptyMap()
        }

        val records =
            database.runQuery {
                QueryDsl
                    .from(bookProductAuthors)
                    .innerJoin(authors) { bookProductAuthors.authorId eq authors.id }
                    .where { bookProductAuthors.bookProductId inList bookProductIds }
                    .selectAsRecord(bookProductAuthors.bookProductId, authors.name)
            }

        return records
            .map { record ->
                requireNotNull(record[bookProductAuthors.bookProductId]) to requireNotNull(record[authors.name])
            }.sortedBy { it.second }
            .groupBy(
                { it.first },
                { it.second },
            )
    }
}
