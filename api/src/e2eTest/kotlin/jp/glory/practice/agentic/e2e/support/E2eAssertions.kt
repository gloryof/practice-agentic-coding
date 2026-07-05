package jp.glory.practice.agentic.e2e.support

import io.restassured.response.Response
import org.hamcrest.Matchers.notNullValue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object E2eAssertions {
    fun assertRegistrationSucceeded(
        response: Response,
        expectedEmail: String,
    ) {
        assertEquals(201, response.statusCode)
        assertEquals(expectedEmail, response.jsonPath().getString("email"))
        assertTrue(
            response.jsonPath().getString("library_user_id").isNotBlank(),
            "library_user_id should be returned",
        )
        assertTrue(
            response.jsonPath().getString("registered_at").isNotBlank(),
            "registered_at should be returned",
        )
    }

    fun assertLoginSucceeded(response: Response) {
        assertEquals(200, response.statusCode)
        assertTrue(
            response.jsonPath().getString("access_token").isNotBlank(),
            "access_token should be returned",
        )
        assertEquals("Bearer", response.jsonPath().getString("token_type"))
        assertTrue(
            response.jsonPath().getLong("expires_in_seconds") > 0,
            "expires_in_seconds should be positive",
        )
    }

    fun assertDuplicateEmailRejected(response: Response) {
        assertEquals(400, response.statusCode)
        assertTrue(response.contentType.contains("application/json"))
        assertEquals("DUPLICATE_EMAIL", response.jsonPath().getString("code"))
    }

    fun assertSearchSucceeded(
        response: Response,
        expectedBookProductId: String,
        expectedTitle: String,
        expectedPublisher: String,
        expectedAuthorName: String,
        expectedIsbn: String,
        expectedAvailableCount: Int,
        expectedTotalCount: Int,
    ) {
        assertEquals(200, response.statusCode)
        response
            .then()
            .body("book_items", notNullValue())
        assertEquals(1, response.jsonPath().getList<Any>("book_items").size)
        assertEquals(expectedBookProductId, response.jsonPath().getString("book_items[0].book_product_id"))
        assertEquals(expectedTitle, response.jsonPath().getString("book_items[0].title"))
        assertEquals(expectedPublisher, response.jsonPath().getString("book_items[0].publisher"))
        assertEquals(expectedAuthorName, response.jsonPath().getString("book_items[0].author_names[0]"))
        assertEquals(expectedIsbn, response.jsonPath().getString("book_items[0].isbn"))
        assertEquals(expectedAvailableCount, response.jsonPath().getInt("book_items[0].available_count"))
        assertEquals(expectedTotalCount, response.jsonPath().getInt("book_items[0].total_count"))
    }

    fun assertReservationPlaced(
        response: Response,
        expectedBookProductId: String,
        expectedTitle: String,
        expectedIsbn: String,
    ) {
        assertEquals(201, response.statusCode)
        assertTrue(
            response.jsonPath().getString("reservation_id").isNotBlank(),
            "reservation_id should be returned",
        )
        assertEquals(expectedBookProductId, response.jsonPath().getString("book_product_id"))
        assertEquals(expectedTitle, response.jsonPath().getString("title"))
        assertEquals(expectedIsbn, response.jsonPath().getString("isbn"))
        assertTrue(
            response.jsonPath().getString("book_item_id").isNotBlank(),
            "book_item_id should be returned",
        )
        assertTrue(
            response.jsonPath().getString("reserved_at").isNotBlank(),
            "reserved_at should be returned",
        )
        assertEquals("ReservationPlacedEvent", response.jsonPath().getString("event_name"))
    }
}
