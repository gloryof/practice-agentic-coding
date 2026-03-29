package jp.glory.practice.agentic.shared.auth

import jp.glory.practice.agentic.shared.web.LoginRequiredApiException
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class AccessTokenAuthenticator(
    private val store: AccessTokenStore,
    private val clock: Clock,
) {
    fun requireValidToken(authorizationHeader: String?) {
        val session = resolveSession(authorizationHeader)
        if (session == null) {
            throw LoginRequiredApiException()
        }
    }

    private fun resolveSession(authorizationHeader: String?): AccessTokenSession? {
        val token = extractBearerToken(authorizationHeader) ?: return null
        val session = store.find(token) ?: return null
        val now = clock.instant()
        if (session.expiresAt.isBefore(now)) {
            store.remove(token)
            return null
        }
        return session
    }

    private fun extractBearerToken(header: String?): String? {
        if (header.isNullOrBlank()) {
            return null
        }
        val prefix = "Bearer "
        if (!header.startsWith(prefix)) {
            return null
        }
        val token = header.removePrefix(prefix).trim()
        return token.takeIf { it.isNotEmpty() }
    }
}
