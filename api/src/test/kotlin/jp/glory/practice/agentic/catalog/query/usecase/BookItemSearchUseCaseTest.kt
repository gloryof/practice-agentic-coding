package jp.glory.practice.agentic.catalog.query.usecase

import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class BookItemSearchUseCaseTest {
    private val defaultInput =
        BookItemSearchInput(
            title = "title",
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

    @Test
    fun `When available count is 1`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)

        every { query.search(defaultInput) } returns
            listOf(
                searchResult(
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎", "佐藤花子"),
                    isbn = "9780000000001",
                    availableCount = 1,
                    totalCount = 2,
                ),
            )

        val result = useCase.search(defaultInput)

        assertResult(
            result = result,
            index = 0,
            expectedTitle = "Kotlin入門",
            expectedPublisher = "技術書房",
            expectedAuthorNames = listOf("山田太郎", "佐藤花子"),
            expectedIsbn = "9780000000001",
            expectedAvailableCount = 1,
            expectedTotalCount = 2,
        )
    }

    @Test
    fun `When available count is 0`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)

        every { query.search(defaultInput) } returns
            listOf(
                searchResult(
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎"),
                    isbn = "9780000000001",
                    availableCount = 0,
                    totalCount = 2,
                ),
            )

        val result = useCase.search(defaultInput)

        assertResult(
            result = result,
            index = 0,
            expectedTitle = "Kotlin入門",
            expectedPublisher = "技術書房",
            expectedAuthorNames = listOf("山田太郎"),
            expectedIsbn = "9780000000001",
            expectedAvailableCount = 0,
            expectedTotalCount = 2,
        )
    }

    @Test
    fun `propagates exception when query fails`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)

        every { query.search(defaultInput) } throws RuntimeException("db error")

        kotlin.test.assertFailsWith<RuntimeException> { useCase.search(defaultInput) }
    }

    @Test
    fun `returns empty list when query returns no results`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)

        every { query.search(defaultInput) } returns emptyList()

        val result = useCase.search(defaultInput)

        assertEquals(0, result.size)
    }

    @Test
    fun `keeps order and maps multiple results`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)

        every { query.search(defaultInput) } returns
            listOf(
                searchResult(
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎"),
                    isbn = "9780000000001",
                    availableCount = 0,
                    totalCount = 2,
                ),
                searchResult(
                    title = "Kotlin実践",
                    publisher = "実装社",
                    authorNames = listOf("佐藤花子"),
                    isbn = "9780000000002",
                    availableCount = 1,
                    totalCount = 1,
                ),
            )

        val result = useCase.search(defaultInput)

        assertEquals(2, result.size)
        assertResult(
            result = result,
            index = 0,
            expectedTitle = "Kotlin入門",
            expectedPublisher = "技術書房",
            expectedAuthorNames = listOf("山田太郎"),
            expectedIsbn = "9780000000001",
            expectedAvailableCount = 0,
            expectedTotalCount = 2,
        )
        assertResult(
            result = result,
            index = 1,
            expectedTitle = "Kotlin実践",
            expectedPublisher = "実装社",
            expectedAuthorNames = listOf("佐藤花子"),
            expectedIsbn = "9780000000002",
            expectedAvailableCount = 1,
            expectedTotalCount = 1,
        )
    }

    private fun searchResult(
        title: String,
        publisher: String,
        authorNames: List<String>,
        isbn: String,
        availableCount: Int,
        totalCount: Int,
    ): BookItemSearchResult =
        BookItemSearchResult(
            title = title,
            publisher = publisher,
            authorNames = authorNames,
            isbn = isbn,
            availableCount = availableCount,
            totalCount = totalCount,
        )

    private fun assertResult(
        result: List<BookItemSearchResults>,
        index: Int,
        expectedTitle: String,
        expectedPublisher: String,
        expectedAuthorNames: List<String>,
        expectedIsbn: String,
        expectedAvailableCount: Int,
        expectedTotalCount: Int,
    ) {
        val item = result[index]
        assertEquals(expectedTitle, item.title)
        assertEquals(expectedPublisher, item.publisher)
        assertEquals(expectedAuthorNames, item.authorNames)
        assertEquals(expectedIsbn, item.isbn)
        assertEquals(expectedAvailableCount, item.availableCount)
        assertEquals(expectedTotalCount, item.totalCount)
    }
}
