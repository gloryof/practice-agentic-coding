package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class BookItemTable(
    @KomapperId
    val id: String,
    val bookProductId: String,
)
