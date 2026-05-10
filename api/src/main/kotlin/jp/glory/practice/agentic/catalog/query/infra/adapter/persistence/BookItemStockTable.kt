package jp.glory.practice.agentic.catalog.query.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
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
