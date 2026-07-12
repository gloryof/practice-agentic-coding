package jp.glory.practice.agentic.e2e.support

import java.util.UUID

internal object E2eFixtures {
    const val password = "Passw0rd!123456"
    const val searchIsbn = "9780000000001"
    const val reservableBookProductId = "book-0002"
    const val reservableBookTitle = "世界史の歩み"
    const val reservableBookIsbn = "9780000000002"
    const val unavailableBookProductId = "book-0001"
    const val duplicateReservationBookProductId = "book-0003"
    val reservationLimitBookProductIds = listOf("book-0005", "book-0006", "book-0009")
    const val reservationLimitRejectedBookProductId = "book-0008"

    fun missingBookProductId(): String = "missing-book-${UUID.randomUUID()}"

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
