package jp.glory.practice.agentic.e2e.catalog

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test

class BookItemSearchE2ETest {
    @Test
    fun `given authenticated user when get book items then returns seeded catalog item`() {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)
        val loginResponse = E2eApiClient.login(user)
        val accessToken = loginResponse.jsonPath().getString("access_token")

        val response = E2eApiClient.searchBookItems(accessToken, E2eFixtures.searchIsbn)

        E2eAssertions.assertSearchSucceeded(
            response = response,
            expectedBookProductId = "book-0001",
            expectedTitle = "世界史入門",
            expectedPublisher = "サンプル出版",
            expectedAuthorName = "ソクラテス",
            expectedIsbn = E2eFixtures.searchIsbn,
            expectedAvailableCount = 0,
            expectedTotalCount = 2,
        )
    }
}
