package jp.glory.practice.agentic.e2e

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertTrue

class PostFixVerificationE2ETest {
    private val baseUrl: String = (System.getenv("API_BASE_URL") ?: "http://localhost:8080").trimEnd('/')

    @Test
    fun `given running api when execute post fix verification flow then all endpoints succeed`() {
        val password = "Passw0rd!123456"
        val email = "e2e-${UUID.randomUUID()}@example.com"

        given()
            .baseUri(baseUrl)
            .disableCsrf()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(mapOf("email" to email, "password" to password))
            .post("/api/v1/library-users/registrations")
            .then()
            .statusCode(201)

        val accessToken =
            given()
                .baseUri(baseUrl)
                .disableCsrf()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(mapOf("email" to email, "password" to password))
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("access_token")

        assertTrue(!accessToken.isNullOrBlank(), "access_token should be returned")

        given()
            .baseUri(baseUrl)
            .disableCsrf()
            .accept(ContentType.JSON)
            .header("Authorization", "Bearer $accessToken")
            .queryParam("isbn", "9780000000001")
            .get("/api/v1/book-items")
            .then()
            .statusCode(200)
            .body("book_items", org.hamcrest.Matchers.notNullValue())
    }
}
