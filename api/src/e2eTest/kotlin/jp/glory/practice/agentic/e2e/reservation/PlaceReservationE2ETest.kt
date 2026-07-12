package jp.glory.practice.agentic.e2e.reservation

import jp.glory.practice.agentic.e2e.support.E2eApiClient
import jp.glory.practice.agentic.e2e.support.E2eAssertions
import jp.glory.practice.agentic.e2e.support.E2eFixtures
import org.junit.jupiter.api.Test

class PlaceReservationE2ETest {
    @Test
    fun `given authenticated user and reservable book product when post reservation then returns placed reservation`() {
        val accessToken = createAuthenticatedUser()

        val response = E2eApiClient.placeReservation(accessToken, E2eFixtures.reservableBookProductId)

        E2eAssertions.assertReservationPlaced(
            response = response,
            expectedBookProductId = E2eFixtures.reservableBookProductId,
            expectedTitle = E2eFixtures.reservableBookTitle,
            expectedIsbn = E2eFixtures.reservableBookIsbn,
        )
    }

    @Test
    fun `given missing book product when post reservation then returns target not found`() {
        val accessToken = createAuthenticatedUser()

        val response = E2eApiClient.placeReservation(accessToken, E2eFixtures.missingBookProductId())

        E2eAssertions.assertReservationTargetNotFound(response)
    }

    @Test
    fun `given book product without available item when post reservation then returns unavailable reason`() {
        val accessToken = createAuthenticatedUser()

        val response = E2eApiClient.placeReservation(accessToken, E2eFixtures.unavailableBookProductId)

        E2eAssertions.assertReservationUnavailable(response, listOf("no_available_book_item"))
    }

    @Test
    fun `given already reserved book product when post reservation then returns unavailable reason`() {
        val accessToken = createAuthenticatedUser()
        val bookProductId = E2eFixtures.duplicateReservationBookProductId
        E2eAssertions.assertReservationAccepted(
            response = E2eApiClient.placeReservation(accessToken, bookProductId),
            expectedBookProductId = bookProductId,
        )

        val response = E2eApiClient.placeReservation(accessToken, bookProductId)

        E2eAssertions.assertReservationUnavailable(response, listOf("already_reserved_book_product"))
    }

    @Test
    fun `given reservation limit reached when post reservation then returns unavailable reason`() {
        val accessToken = createAuthenticatedUser()
        E2eFixtures.reservationLimitBookProductIds.forEach { bookProductId ->
            E2eAssertions.assertReservationAccepted(
                response = E2eApiClient.placeReservation(accessToken, bookProductId),
                expectedBookProductId = bookProductId,
            )
        }

        val response =
            E2eApiClient.placeReservation(
                accessToken,
                E2eFixtures.reservationLimitRejectedBookProductId,
            )

        E2eAssertions.assertReservationUnavailable(response, listOf("reservation_limit_reached"))
    }

    private fun createAuthenticatedUser(): String {
        val user = E2eFixtures.createUser()
        E2eAssertions.assertRegistrationSucceeded(E2eApiClient.registerUser(user), user.email)
        val loginResponse = E2eApiClient.login(user)
        E2eAssertions.assertLoginSucceeded(loginResponse)
        return loginResponse.jsonPath().getString("access_token")
    }
}
