package jp.glory.practice.agentic.shared.auth

interface AccessTokenStore {
    fun save(session: AccessTokenSession)

    fun find(token: String): AccessTokenSession?

    fun remove(token: String)
}
