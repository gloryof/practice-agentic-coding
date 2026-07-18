package jp.glory.practice.agentic.auth.command.domain.event

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.shared.auth.AccessTokenSession
import java.time.Instant

data class AuthLoggedInEvent(
    val account: AuthAccount,
    val accessToken: String,
    val expiresAt: Instant,
    val occurredAt: Instant,
) {
    fun toAccessTokenSession(): AccessTokenSession =
        AccessTokenSession(
            token = accessToken,
            libraryUserId = account.libraryUserId.value,
            expiresAt = expiresAt,
        )
}

fun interface AuthLoggedInEventHandler {
    fun handle(event: AuthLoggedInEvent)
}
