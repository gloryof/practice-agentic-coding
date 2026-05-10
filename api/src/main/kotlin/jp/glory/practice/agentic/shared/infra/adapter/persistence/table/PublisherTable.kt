package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable

@KomapperEntity
@KomapperTable(name = "publishers")
data class PublisherTable(
    @KomapperId
    val id: String,
    val name: String,
    val nameKana: String,
)
