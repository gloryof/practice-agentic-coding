package jp.glory.practice.agentic.catalog.query.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class BookItemAuthorTable(
    @KomapperId
    val bookItemId: String,
    @KomapperId
    val authorId: String,
)
