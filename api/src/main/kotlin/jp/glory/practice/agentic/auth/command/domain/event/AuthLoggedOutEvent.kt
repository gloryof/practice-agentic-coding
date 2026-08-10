package jp.glory.practice.agentic.auth.command.domain.event

import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import java.time.Instant

data class AuthLoggedOutEvent(
    val libraryUserId: LibraryUserId,
    val accessToken: String,
    val occurredAt: Instant,
)

fun interface AuthLoggedOutEventHandler {
    fun handle(event: AuthLoggedOutEvent)
}
