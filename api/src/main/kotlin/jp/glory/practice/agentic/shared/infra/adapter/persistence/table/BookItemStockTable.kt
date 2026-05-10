package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable

@KomapperEntity
@KomapperTable(name = "book_item_stocks")
data class BookItemStockTable(
    @KomapperId
    val id: String,
    val bookItemId: String,
    val status: BookItemStockStatus,
)

enum class BookItemStockStatus {
    AVAILABLE,
    CHECKED_OUT,
}
