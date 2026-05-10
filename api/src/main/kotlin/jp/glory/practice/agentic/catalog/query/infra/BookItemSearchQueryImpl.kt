package jp.glory.practice.agentic.catalog.query.infra

import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchQuery
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchResult
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookItemStockDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookProductAuthorDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookProductDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.StockCount
import org.springframework.stereotype.Repository

@Repository
class BookItemSearchQueryImpl(
    private val bookProductDao: BookProductDao,
    private val bookProductAuthorDao: BookProductAuthorDao,
    private val bookItemStockDao: BookItemStockDao,
) : BookItemSearchQuery {
    override fun search(input: BookItemSearchInput): List<BookItemSearchResult> {
        val bookProductRows = bookProductDao.findBySearchInput(input)
        if (bookProductRows.isEmpty()) {
            return emptyList()
        }

        val bookProductIds = bookProductRows.map { it.id }
        val authorMap = bookProductAuthorDao.findAuthorNamesByBookProductIds(bookProductIds)
        val stockCountMap = bookItemStockDao.countByBookProductIds(bookProductIds)

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
}
