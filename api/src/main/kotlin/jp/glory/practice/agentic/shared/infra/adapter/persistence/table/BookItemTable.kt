package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable

@KomapperEntity
@KomapperTable(name = "book_items")
data class BookItemTable(
    @KomapperId
    val id: String,
    val bookProductId: String,
)
