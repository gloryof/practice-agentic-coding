package jp.glory.practice.agentic.e2e.auth

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class LogoutE2ETest {
    @Test
    fun `given two sessions when logout current session then rejects only logged out token`() {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)
        val loggedOutLogin = E2eApiClient.login(user)
        val activeLogin = E2eApiClient.login(user)
        E2eAssertions.assertLoginSucceeded(loggedOutLogin)
        E2eAssertions.assertLoginSucceeded(activeLogin)
        val loggedOutToken = loggedOutLogin.jsonPath().getString("access_token")
        val activeToken = activeLogin.jsonPath().getString("access_token")
        assertNotEquals(loggedOutToken, activeToken)

        E2eAssertions.assertLogoutSucceeded(E2eApiClient.logout(loggedOutToken))

        E2eAssertions.assertLoginRequired(E2eApiClient.logout(loggedOutToken))
        E2eAssertions.assertLoginRequired(
            E2eApiClient.searchBookItems(loggedOutToken, E2eFixtures.searchIsbn),
        )
        E2eAssertions.assertSearchSucceeded(
            response = E2eApiClient.searchBookItems(activeToken, E2eFixtures.searchIsbn),
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
