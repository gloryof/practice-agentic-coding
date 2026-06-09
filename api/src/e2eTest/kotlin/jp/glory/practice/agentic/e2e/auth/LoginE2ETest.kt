package jp.glory.practice.agentic.e2e.auth

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test

class LoginE2ETest {
    @Test
    fun `given registered user when post login then returns access token`() {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)

        val response = E2eApiClient.login(user)

        E2eAssertions.assertLoginSucceeded(response)
    }
}
