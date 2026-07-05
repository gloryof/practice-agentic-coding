package jp.glory.practice.agentic.catalog.query.web

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchResults
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import jp.glory.practice.agentic.shared.spring.GlobalExceptionHandler
import jp.glory.practice.agentic.shared.web.LoginRequiredApiException
import org.junit.jupiter.api.Nested
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookItemSearchControllerTest {
    private data class TestContext(
        val mvc: MockMvc,
        val useCase: BookItemSearchUseCase,
        val authenticator: AccessTokenAuthenticator,
    )

    @Nested
    inner class Search {
        @Test
        fun `given valid criteria when get book items then returns 200`() {
            val context = createSut()
            val input =
                BookItemSearchInput(
                    title = "Kotlin",
                    titleExact = false,
                    titleKana = null,
                    titleKanaExact = false,
                    publisher = null,
                    publisherExact = false,
                    publisherKana = null,
                    publisherKanaExact = false,
                    authorName = null,
                    authorExact = false,
                    authorNameKana = null,
                    authorKanaExact = false,
                    isbn = null,
                )
            every { context.useCase.search(input) } returns
                listOf(
                    BookItemSearchResults(
                        bookProductId = "book-1",
                        title = "Kotlin入門",
                        publisher = "技術書房",
                        authorNames = listOf("山田太郎"),
                        isbn = "9780000000001",
                        availableCount = 1,
                        totalCount = 2,
                    ),
                )

            context.mvc
                .perform(
                    get("/api/v1/book-items")
                        .param("title", "Kotlin")
                        .header("Authorization", "Bearer token-123")
                        .accept(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.book_items[0].book_product_id").value("book-1"))
                .andExpect(jsonPath("$.book_items[0].author_names[0]").value("山田太郎"))
                .andExpect(jsonPath("$.book_items[0].available_count").value(1))
                .andExpect(jsonPath("$.book_items[0].total_count").value(2))
            verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
        }

        @Test
        fun `given no results when get book items then returns 200 with empty list`() {
            val context = createSut()
            val input =
                BookItemSearchInput(
                    title = "Kotlin",
                    titleExact = false,
                    titleKana = null,
                    titleKanaExact = false,
                    publisher = null,
                    publisherExact = false,
                    publisherKana = null,
                    publisherKanaExact = false,
                    authorName = null,
                    authorExact = false,
                    authorNameKana = null,
                    authorKanaExact = false,
                    isbn = null,
                )
            every { context.useCase.search(input) } returns emptyList()

            context.mvc
                .perform(
                    get("/api/v1/book-items")
                        .param("title", "Kotlin")
                        .header("Authorization", "Bearer token-123")
                        .accept(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.book_items.length()").value(0))
            verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
        }

        @Test
        fun `given multiple results when get book items then returns 200 with all items`() {
            val context = createSut()
            val input =
                BookItemSearchInput(
                    title = "Kotlin",
                    titleExact = false,
                    titleKana = null,
                    titleKanaExact = false,
                    publisher = null,
                    publisherExact = false,
                    publisherKana = null,
                    publisherKanaExact = false,
                    authorName = null,
                    authorExact = false,
                    authorNameKana = null,
                    authorKanaExact = false,
                    isbn = null,
                )
            every { context.useCase.search(input) } returns
                listOf(
                    BookItemSearchResults(
                        bookProductId = "book-1",
                        title = "Kotlin入門",
                        publisher = "技術書房",
                        authorNames = listOf("山田太郎"),
                        isbn = "9780000000001",
                        availableCount = 0,
                        totalCount = 2,
                    ),
                    BookItemSearchResults(
                        bookProductId = "book-2",
                        title = "Kotlin実践",
                        publisher = "技術書房",
                        authorNames = listOf("佐藤花子"),
                        isbn = "9780000000002",
                        availableCount = 1,
                        totalCount = 1,
                    ),
                )

            context.mvc
                .perform(
                    get("/api/v1/book-items")
                        .param("title", "Kotlin")
                        .header("Authorization", "Bearer token-123")
                        .accept(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.book_items.length()").value(2))
                .andExpect(jsonPath("$.book_items[0].title").value("Kotlin入門"))
                .andExpect(jsonPath("$.book_items[1].title").value("Kotlin実践"))
                .andExpect(jsonPath("$.book_items[0].available_count").value(0))
                .andExpect(jsonPath("$.book_items[0].total_count").value(2))
                .andExpect(jsonPath("$.book_items[1].available_count").value(1))
                .andExpect(jsonPath("$.book_items[1].total_count").value(1))
            verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
        }

        @Test
        fun `given use case throws unexpected exception when get book items then returns 500`() {
            val context = createSut()
            every { context.useCase.search(any()) } throws RuntimeException("db error")

            val response =
                context.mvc
                    .perform(
                        get("/api/v1/book-items")
                            .param("title", "Kotlin")
                            .header("Authorization", "Bearer token-123")
                            .accept(MediaType.APPLICATION_JSON),
                    ).andReturn()
                    .response

            assertEquals(500, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"INTERNAL_SERVER_ERROR\""))
            verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
        }

        @Test
        fun `given unauthenticated request when get book items then returns 401`() {
            val context = createSut()
            every { context.authenticator.requireValidToken(null) } throws LoginRequiredApiException()

            val response =
                context.mvc
                    .perform(
                        get("/api/v1/book-items")
                            .param("title", "Kotlin")
                            .accept(MediaType.APPLICATION_JSON),
                    ).andReturn()
                    .response

            assertEquals(401, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"LOGIN_REQUIRED\""))
            verify(exactly = 0) { context.useCase.search(any()) }
        }

        @Test
        fun `given missing criteria when get book items then returns 400`() {
            val context = createSut()

            val response =
                context.mvc
                    .perform(
                        get("/api/v1/book-items")
                            .header("Authorization", "Bearer token-123")
                            .accept(MediaType.APPLICATION_JSON),
                    ).andReturn()
                    .response

            assertEquals(400, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
            verify(exactly = 0) { context.useCase.search(any()) }
        }

        @Test
        fun `given exact flag without value when get book items then returns 400`() {
            val context = createSut()

            val response =
                context.mvc
                    .perform(
                        get("/api/v1/book-items")
                            .param("title_exact", "true")
                            .header("Authorization", "Bearer token-123")
                            .accept(MediaType.APPLICATION_JSON),
                    ).andReturn()
                    .response

            assertEquals(400, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
            assertTrue(response.contentAsString.contains("\"field\":\"title_exact\""))
            assertTrue(response.contentAsString.contains("\"reason\":\"requires_value\""))
            assertTrue(response.contentAsString.contains("\"field\":\"criteria\""))
            verify(exactly = 0) { context.useCase.search(any()) }
        }

        @Test
        fun `given exact flag false when get book items then returns 400`() {
            val context = createSut()

            val response =
                context.mvc
                    .perform(
                        get("/api/v1/book-items")
                            .param("title", "Kotlin")
                            .param("title_exact", "false")
                            .header("Authorization", "Bearer token-123")
                            .accept(MediaType.APPLICATION_JSON),
                    ).andReturn()
                    .response

            assertEquals(400, response.status)
            assertTrue(response.contentAsString.contains("\"code\":\"VALIDATION_ERROR\""))
            assertTrue(response.contentAsString.contains("\"field\":\"title_exact\""))
            assertTrue(response.contentAsString.contains("\"reason\":\"must_be_true\""))
            verify(exactly = 0) { context.useCase.search(any()) }
        }
    }

    private fun createSut(
        useCase: BookItemSearchUseCase = mockk(relaxed = true),
        authenticator: AccessTokenAuthenticator = mockk(relaxed = true),
    ): TestContext {
        val builder: StandaloneMockMvcBuilder =
            MockMvcBuilders
                .standaloneSetup(
                    BookItemSearchController(
                        validator = BookItemSearchRequestValidator(),
                        useCase = useCase,
                        authenticator = authenticator,
                    ),
                )
        builder.setControllerAdvice(GlobalExceptionHandler())
        return TestContext(
            mvc = builder.build(),
            useCase = useCase,
            authenticator = authenticator,
        )
    }
}
