package jp.glory.practice.agentic.catalog.query.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class BookProductAuthorTable(
    @KomapperId
    val bookProductId: String,
    @KomapperId
    val authorId: String,
)
