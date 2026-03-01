package jp.glory.practice.agentic.auth.command.domain.model

import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId

data class AuthCredential(
    val libraryUserId: LibraryUserId,
    val passwordHash: PasswordHash,
)
