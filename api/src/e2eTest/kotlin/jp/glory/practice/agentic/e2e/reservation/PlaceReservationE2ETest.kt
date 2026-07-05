package jp.glory.practice.agentic.e2e.reservation

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test

class PlaceReservationE2ETest {
    @Test
    fun `given authenticated user and reservable book product when post reservation then returns placed reservation`() {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)
        val loginResponse = E2eApiClient.login(user)
        val accessToken = loginResponse.jsonPath().getString("access_token")

        val response = E2eApiClient.placeReservation(accessToken, E2eFixtures.reservableBookProductId)

        E2eAssertions.assertReservationPlaced(
            response = response,
            expectedBookProductId = E2eFixtures.reservableBookProductId,
            expectedTitle = E2eFixtures.reservableBookTitle,
            expectedIsbn = E2eFixtures.reservableBookIsbn,
        )
    }
}
