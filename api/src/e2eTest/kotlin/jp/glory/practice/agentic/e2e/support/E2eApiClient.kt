package jp.glory.practice.agentic.e2e.support

import io.restassured.RestAssured.given
import io.restassured.http.ContentType

internal object E2eApiClient {
    private val baseUrl: String =
        (System.getenv("API_BASE_URL") ?: "http://localhost:8080").trimEnd('/')

    fun registerUser(user: E2eUserCredentials) =
        given()
            .baseUri(baseUrl)
            .disableCsrf()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                mapOf(
                    "email" to user.email,
                    "password" to user.password,
                ),
            ).post("/api/v1/library-users/registrations")

    fun login(user: E2eUserCredentials) =
        given()
            .baseUri(baseUrl)
            .disableCsrf()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                mapOf(
                    "email" to user.email,
                    "password" to user.password,
                ),
            ).post("/api/v1/auth/login")

    fun searchBookItems(
        accessToken: String,
        isbn: String,
    ) = given()
        .baseUri(baseUrl)
        .disableCsrf()
        .accept(ContentType.JSON)
        .header("Authorization", "Bearer $accessToken")
        .queryParam("isbn", isbn)
        .get("/api/v1/book-items")
}
