package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookItemStockStatus
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemStockTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemTable
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.jdbc.JdbcDatabase
import org.springframework.stereotype.Repository

@Repository
class BookItemStockDao(
    private val database: JdbcDatabase,
) {
    private val bookItems = Meta.bookItemTable
    private val stocks = Meta.bookItemStockTable

    fun countByBookProductIds(bookProductIds: List<String>): Map<String, StockCount> {
        if (bookProductIds.isEmpty()) {
            return emptyMap()
        }

        val records =
            database.runQuery {
                QueryDsl
                    .from(bookItems)
                    .innerJoin(stocks) { bookItems.id eq stocks.bookItemId }
                    .where { bookItems.bookProductId inList bookProductIds }
                    .selectAsRecord(bookItems.bookProductId, stocks.status)
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

data class StockCount(
    val availableCount: Int,
    val totalCount: Int,
)
