package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductAuthorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.publisherTable
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.expression.ColumnExpression
import org.komapper.core.dsl.operator.lower
import org.komapper.core.dsl.scope.WhereScope
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class BookProductDao(
    private val database: JdbcDatabase,
) {
    private val bookProducts = Meta.bookProductTable
    private val publishers = Meta.publisherTable
    private val authors = Meta.authorTable
    private val bookProductAuthors = Meta.bookProductAuthorTable

    fun findBySearchInput(input: BookItemSearchInput): List<BookProductRow> {
        val records =
            database.runQuery {
                QueryDsl
                    .from(bookProducts)
                    .innerJoin(publishers) { bookProducts.publisherId eq publishers.id }
                    .where { applySearchConditions(input) }
                    .selectAsRecord(
                        bookProducts.id,
                        bookProducts.title,
                        bookProducts.isbn,
                        publishers.name,
                    )
            }

        return records.map { record ->
            BookProductRow(
                id = requireNotNull(record[bookProducts.id]),
                title = requireNotNull(record[bookProducts.title]),
                isbn = requireNotNull(record[bookProducts.isbn]),
                publisher = requireNotNull(record[publishers.name]),
            )
        }
    }

    private fun WhereScope.applySearchConditions(input: BookItemSearchInput) {
        addMatchCondition(input.title, input.titleExact, bookProducts.title)
        addMatchCondition(input.titleKana, input.titleKanaExact, bookProducts.titleKana)
        addMatchCondition(input.publisher, input.publisherExact, publishers.name)
        addMatchCondition(input.publisherKana, input.publisherKanaExact, publishers.nameKana)
        addAuthorConditions(input)
        addIsbnCondition(input.isbn)
    }

    private fun WhereScope.addAuthorConditions(input: BookItemSearchInput) {
        val hasName = !input.authorName.isNullOrBlank()
        val hasNameKana = !input.authorNameKana.isNullOrBlank()
        if (!hasName && !hasNameKana) {
            return
        }

        exists(
            QueryDsl
                .from(bookProductAuthors)
                .innerJoin(authors) { bookProductAuthors.authorId eq authors.id }
                .where {
                    bookProductAuthors.bookProductId eq bookProducts.id
                    addMatchCondition(input.authorName, input.authorExact, authors.name)
                    addMatchCondition(input.authorNameKana, input.authorKanaExact, authors.nameKana)
                }.select(bookProductAuthors.bookProductId),
        )
    }

    private fun WhereScope.addIsbnCondition(isbn: String?) {
        val trimmed = isbn?.trim()
        if (!trimmed.isNullOrBlank()) {
            bookProducts.isbn eq trimmed
        }
    }

    private fun WhereScope.addMatchCondition(
        value: String?,
        exact: Boolean,
        column: ColumnExpression<String, String>,
    ) {
        val trimmed = value?.trim()
        if (trimmed.isNullOrBlank()) {
            return
        }
        val lowered = trimmed.lowercase()
        if (exact) {
            lower(column) eq lowered
        } else {
            lower(column) like "%$lowered%"
        }
    }
}

data class BookProductRow(
    val id: String,
    val title: String,
    val isbn: String,
    val publisher: String,
)
