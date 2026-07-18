package jp.glory.practice.agentic.auth.command.domain.model

data class AuthCredential(
    val libraryUserId: LibraryUserId,
    val passwordHash: PasswordHash,
)
