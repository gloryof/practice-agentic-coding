package jp.glory.practice.agentic.e2e.support

import java.util.UUID

internal object E2eFixtures {
    const val password = "Passw0rd!123456"
    const val searchIsbn = "9780000000001"

    fun createUser(): E2eUserCredentials =
        E2eUserCredentials(
            email = "e2e-${UUID.randomUUID()}@example.com",
            password = password,
        )
}

internal data class E2eUserCredentials(
    val email: String,
    val password: String,
)
