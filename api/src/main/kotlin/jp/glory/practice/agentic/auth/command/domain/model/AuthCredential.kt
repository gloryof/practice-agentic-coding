package jp.glory.practice.agentic.auth.command.domain.model

data class AuthCredential(
    val libraryUserId: String,
    val passwordHash: String,
)
