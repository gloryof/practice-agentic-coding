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

    @Test
    fun `returns 200 on success`() {
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
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎"),
                    isbn = "9780000000001",
                ),
            )

        context.mvc
            .perform(
                get("/api/v1/book-items")
                    .param("title", "Kotlin")
                    .header("Authorization", "Bearer token-123")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.book_items[0].author_names[0]").value("山田太郎"))
        verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
    }

    @Test
    fun `returns 200 with empty list when no results`() {
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
    fun `returns 200 with multiple items`() {
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
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎"),
                    isbn = "9780000000001",
                ),
                BookItemSearchResults(
                    title = "Kotlin実践",
                    publisher = "技術書房",
                    authorNames = listOf("佐藤花子"),
                    isbn = "9780000000002",
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
        verify(exactly = 1) { context.authenticator.requireValidToken("Bearer token-123") }
    }

    @Test
    fun `returns 401 when unauthenticated`() {
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
    fun `returns 400 when criteria missing`() {
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
    fun `returns 400 when exact flag without value`() {
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
    fun `returns 400 when exact flag is not true`() {
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
