package jp.glory.practice.agentic.catalog.query.infra

import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.BookItemStockStatus
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.authorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookItemStockTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookItemTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookProductAuthorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookProductTable
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
    private val bookProducts = Meta.bookProductTable.clone(table = "book_products")
    private val publishers = Meta.publisherTable.clone(table = "publishers")
    private val authors = Meta.authorTable.clone(table = "authors")
    private val bookProductAuthors = Meta.bookProductAuthorTable.clone(table = "book_product_authors")
    private val stocks = Meta.bookItemStockTable.clone(table = "book_item_stocks")

    override fun search(input: BookItemSearchInput): List<BookItemSearchResult> {
        val bookProductRows = queryBookProducts(input)
        if (bookProductRows.isEmpty()) {
            return emptyList()
        }

        val bookProductIds = bookProductRows.map { it.id }
        val authorMap = loadAuthors(bookProductIds)
        val stockCountMap = loadStockCountMap(bookProductIds)

        return bookProductRows.map { row ->
            val stockCount = stockCountMap[row.id] ?: StockCount(availableCount = 0, totalCount = 0)
            BookItemSearchResult(
                title = row.title,
                publisher = row.publisher,
                authorNames = authorMap[row.id] ?: emptyList(),
                isbn = row.isbn,
                availableCount = stockCount.availableCount,
                totalCount = stockCount.totalCount,
            )
        }
    }

    private fun queryBookProducts(input: BookItemSearchInput): List<BookProductRow> {
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

    private fun loadAuthors(bookProductIds: List<String>): Map<String, List<String>> {
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

    private fun loadStockCountMap(bookProductIds: List<String>): Map<String, StockCount> {
        val records =
            database.runQuery {
                QueryDsl
                    .from(bookItems)
                    .innerJoin(stocks) { bookItems.id eq stocks.bookItemId }
                    .where {
                        bookItems.bookProductId inList bookProductIds
                    }.selectAsRecord(bookItems.bookProductId, stocks.status)
            }

        return records
            .groupBy { requireNotNull(it[bookItems.bookProductId]) }
            .mapValues { (_, grouped) ->
                val availableCount =
                    grouped.count { requireNotNull(it[stocks.status]) == BookItemStockStatus.AVAILABLE }
                StockCount(availableCount = availableCount, totalCount = grouped.size)
            }
    }
}

private data class BookProductRow(
    val id: String,
    val title: String,
    val isbn: String,
    val publisher: String,
)

private data class StockCount(
    val availableCount: Int,
    val totalCount: Int,
)
