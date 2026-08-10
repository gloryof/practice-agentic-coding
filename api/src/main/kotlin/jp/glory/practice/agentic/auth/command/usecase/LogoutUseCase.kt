package jp.glory.practice.agentic.auth.command.usecase

import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEvent
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEventHandler
import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class LogoutUseCase(
    private val authLoggedOutEventHandler: AuthLoggedOutEventHandler,
    private val clock: Clock,
) {
    fun logout(input: LogoutInput) {
        authLoggedOutEventHandler.handle(
            AuthLoggedOutEvent(
                libraryUserId = LibraryUserId(input.libraryUserId),
                accessToken = input.accessToken,
                occurredAt = clock.instant(),
            ),
        )
    }
}

data class LogoutInput(
    val libraryUserId: String,
    val accessToken: String,
)
