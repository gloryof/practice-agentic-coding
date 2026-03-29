package jp.glory.practice.agentic.shared.auth

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryAccessTokenStore : AccessTokenStore {
    private val sessions = ConcurrentHashMap<String, AccessTokenSession>()

    override fun save(session: AccessTokenSession) {
        sessions[session.token] = session
    }

    override fun find(token: String): AccessTokenSession? = sessions[token]

    override fun remove(token: String) {
        sessions.remove(token)
    }
}
