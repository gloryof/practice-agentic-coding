package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable

@KomapperEntity
@KomapperTable(name = "book_product_authors")
data class BookProductAuthorTable(
    @KomapperId
    val bookProductId: String,
    @KomapperId
    val authorId: String,
)
