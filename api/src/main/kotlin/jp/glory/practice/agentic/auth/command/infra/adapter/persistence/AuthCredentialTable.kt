package jp.glory.practice.agentic.auth.command.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId

@KomapperEntity
data class AuthCredentialTable(
    @KomapperId
    val libraryUserId: String,
    val passwordHash: String,
)
