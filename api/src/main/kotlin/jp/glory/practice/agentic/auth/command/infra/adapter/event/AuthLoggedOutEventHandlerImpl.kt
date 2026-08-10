package jp.glory.practice.agentic.auth.command.infra.adapter.event

import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEvent
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedOutEventHandler
import jp.glory.practice.agentic.shared.auth.AccessTokenStore
import org.springframework.stereotype.Component

@Component
class AuthLoggedOutEventHandlerImpl(
    private val accessTokenStore: AccessTokenStore,
) : AuthLoggedOutEventHandler {
    override fun handle(event: AuthLoggedOutEvent) {
        accessTokenStore.remove(event.accessToken)
    }
}
