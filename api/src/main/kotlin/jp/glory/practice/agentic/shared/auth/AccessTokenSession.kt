package jp.glory.practice.agentic.shared.auth
import java.time.Instant

data class AccessTokenSession(
    val token: String,
    val libraryUserId: String,
    val expiresAt: Instant,
)
