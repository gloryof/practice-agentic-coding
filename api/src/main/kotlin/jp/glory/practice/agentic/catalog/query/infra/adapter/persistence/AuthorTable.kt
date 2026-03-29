package jp.glory.practice.agentic.catalog.query.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class AuthorTable(
    @KomapperId
    val id: String,
    val name: String,
    val nameKana: String,
)
