package jp.glory.practice.agentic.e2e.libraryuser

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test

class LibraryUserRegistrationE2ETest {
    @Test
    fun `given valid request when post registration then returns created response`() {
        val user = E2eFixtures.createUser()

        val response = E2eApiClient.registerUser(user)

        E2eAssertions.assertRegistrationSucceeded(response, user.email)
    }

    @Test
    fun `given duplicate email when post registration then returns duplicate email response`() {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)

        val response = E2eApiClient.registerUser(user)

        E2eAssertions.assertDuplicateEmailRejected(response)
    }
}
