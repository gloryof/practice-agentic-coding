package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

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
