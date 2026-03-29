package jp.glory.practice.agentic.catalog.query.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class BookItemTable(
    @KomapperId
    val id: String,
    val title: String,
    val titleKana: String,
    val publisherId: String,
    val isbn: String,
)
