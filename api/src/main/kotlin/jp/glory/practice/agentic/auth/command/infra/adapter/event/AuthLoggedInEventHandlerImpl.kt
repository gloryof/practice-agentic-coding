package jp.glory.practice.agentic.auth.command.infra.adapter.event

import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedInEvent
import jp.glory.practice.agentic.auth.command.domain.event.AuthLoggedInEventHandler
import jp.glory.practice.agentic.shared.auth.AccessTokenStore
import org.springframework.stereotype.Component

@Component
class AuthLoggedInEventHandlerImpl(
    private val accessTokenStore: AccessTokenStore,
) : AuthLoggedInEventHandler {
    override fun handle(event: AuthLoggedInEvent) {
        accessTokenStore.save(event.toAccessTokenSession())
    }
}
