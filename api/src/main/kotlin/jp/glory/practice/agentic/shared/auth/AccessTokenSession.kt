package jp.glory.practice.agentic.shared.auth

import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import java.time.Instant

data class AccessTokenSession(
    val token: String,
    val libraryUserId: LibraryUserId,
    val expiresAt: Instant,
)
