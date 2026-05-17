package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable

@KomapperEntity
@KomapperTable(name = "auth_credentials")
data class AuthCredentialTable(
    @KomapperId
    val id: String,
    val libraryUserId: String,
    val passwordHash: String,
)
