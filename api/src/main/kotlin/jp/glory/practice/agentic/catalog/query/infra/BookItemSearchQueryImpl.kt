package jp.glory.practice.agentic.catalog.query.infra

import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.authorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookItemAuthorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookItemTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.publisherTable
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchQuery
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchResult
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.expression.ColumnExpression
import org.komapper.core.dsl.operator.lower
import org.komapper.core.dsl.scope.WhereScope
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class BookItemSearchQueryImpl(
    private val database: JdbcDatabase,
) : BookItemSearchQuery {
    private val bookItems = Meta.bookItemTable.clone(table = "book_items")
    private val publishers = Meta.publisherTable.clone(table = "publishers")
    private val authors = Meta.authorTable.clone(table = "authors")
    private val bookItemAuthors = Meta.bookItemAuthorTable.clone(table = "book_item_authors")

    override fun search(input: BookItemSearchInput): List<BookItemSearchResult> {
        val bookItemRows = queryBookItems(input)
        if (bookItemRows.isEmpty()) {
            return emptyList()
        }

        val authorMap = loadAuthors(bookItemRows.map { it.id })

        return bookItemRows.map { row ->
            BookItemSearchResult(
                bookItemId = row.id,
                title = row.title,
                publisher = row.publisher,
                authorNames = authorMap[row.id] ?: emptyList(),
                isbn = row.isbn,
            )
        }
    }

    private fun queryBookItems(input: BookItemSearchInput): List<BookItemRow> {
        val records =
            database.runQuery {
                QueryDsl
                    .from(bookItems)
                    .innerJoin(publishers) { bookItems.publisherId eq publishers.id }
                    .where { applySearchConditions(input) }
                    .selectAsRecord(
                        bookItems.id,
                        bookItems.title,
                        bookItems.isbn,
                        publishers.name,
                    )
            }

        return records.map { record ->
            BookItemRow(
                id = requireNotNull(record[bookItems.id]),
                title = requireNotNull(record[bookItems.title]),
                isbn = requireNotNull(record[bookItems.isbn]),
                publisher = requireNotNull(record[publishers.name]),
            )
        }
    }

    private fun WhereScope.applySearchConditions(input: BookItemSearchInput) {
        addMatchCondition(input.title, input.titleExact, bookItems.title)
        addMatchCondition(input.titleKana, input.titleKanaExact, bookItems.titleKana)
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
                .from(bookItemAuthors)
                .innerJoin(authors) { bookItemAuthors.authorId eq authors.id }
                .where {
                    bookItemAuthors.bookItemId eq bookItems.id
                    addMatchCondition(input.authorName, input.authorExact, authors.name)
                    addMatchCondition(input.authorNameKana, input.authorKanaExact, authors.nameKana)
                }.select(bookItemAuthors.bookItemId),
        )
    }

    private fun WhereScope.addIsbnCondition(isbn: String?) {
        val trimmed = isbn?.trim()
        if (!trimmed.isNullOrBlank()) {
            bookItems.isbn eq trimmed
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

    private fun loadAuthors(bookItemIds: List<String>): Map<String, List<String>> {
        val records =
            database.runQuery {
                QueryDsl
                    .from(bookItemAuthors)
                    .innerJoin(authors) { bookItemAuthors.authorId eq authors.id }
                    .where { bookItemAuthors.bookItemId inList bookItemIds }
                    .selectAsRecord(bookItemAuthors.bookItemId, authors.name)
            }

        return records
            .map { record ->
                requireNotNull(record[bookItemAuthors.bookItemId]) to requireNotNull(record[authors.name])
            }.sortedBy { it.second }
            .groupBy(
                { it.first },
                { it.second },
            )
    }
}

private data class BookItemRow(
    val id: String,
    val title: String,
    val isbn: String,
    val publisher: String,
)
