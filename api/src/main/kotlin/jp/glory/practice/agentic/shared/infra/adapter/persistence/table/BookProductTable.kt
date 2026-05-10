package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class BookProductTable(
    @KomapperId
    val id: String,
    val title: String,
    val titleKana: String,
    val publisherId: String,
    val isbn: String,
)
