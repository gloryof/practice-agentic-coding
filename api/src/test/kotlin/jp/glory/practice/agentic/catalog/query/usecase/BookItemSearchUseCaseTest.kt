package jp.glory.practice.agentic.catalog.query.usecase

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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

    @Nested
    inner class Search {
        @Test
        fun `given search result with available count 1 when search then maps result`() {
            val query = mockk<BookItemSearchQuery>()
            val useCase = BookItemSearchUseCase(query)

            every { query.search(defaultInput) } returns
                listOf(
                    searchResult(
                        bookProductId = "book-1",
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
                expectedBookProductId = "book-1",
                expectedPublisher = "技術書房",
                expectedAuthorNames = listOf("山田太郎", "佐藤花子"),
                expectedIsbn = "9780000000001",
                expectedAvailableCount = 1,
                expectedTotalCount = 2,
            )
        }

        @Test
        fun `given search result with available count 0 when search then maps result`() {
            val query = mockk<BookItemSearchQuery>()
            val useCase = BookItemSearchUseCase(query)

            every { query.search(defaultInput) } returns
                listOf(
                    searchResult(
                        bookProductId = "book-1",
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
                expectedBookProductId = "book-1",
                expectedPublisher = "技術書房",
                expectedAuthorNames = listOf("山田太郎"),
                expectedIsbn = "9780000000001",
                expectedAvailableCount = 0,
                expectedTotalCount = 2,
            )
        }

        @Test
        fun `given query failure when search then propagates exception`() {
            val query = mockk<BookItemSearchQuery>()
            val useCase = BookItemSearchUseCase(query)

            every { query.search(defaultInput) } throws RuntimeException("db error")

            kotlin.test.assertFailsWith<RuntimeException> { useCase.search(defaultInput) }
        }

        @Test
        fun `given empty query results when search then returns empty list`() {
            val query = mockk<BookItemSearchQuery>()
            val useCase = BookItemSearchUseCase(query)

            every { query.search(defaultInput) } returns emptyList()

            val result = useCase.search(defaultInput)

            assertEquals(0, result.size)
        }

        @Test
        fun `given multiple query results when search then keeps order and maps all`() {
            val query = mockk<BookItemSearchQuery>()
            val useCase = BookItemSearchUseCase(query)

            every { query.search(defaultInput) } returns
                listOf(
                    searchResult(
                        bookProductId = "book-1",
                        title = "Kotlin入門",
                        publisher = "技術書房",
                        authorNames = listOf("山田太郎"),
                        isbn = "9780000000001",
                        availableCount = 0,
                        totalCount = 2,
                    ),
                    searchResult(
                        bookProductId = "book-2",
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
                expectedBookProductId = "book-1",
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
                expectedBookProductId = "book-2",
                expectedPublisher = "実装社",
                expectedAuthorNames = listOf("佐藤花子"),
                expectedIsbn = "9780000000002",
                expectedAvailableCount = 1,
                expectedTotalCount = 1,
            )
        }
    }

    private fun searchResult(
        bookProductId: String,
        title: String,
        publisher: String,
        authorNames: List<String>,
        isbn: String,
        availableCount: Int,
        totalCount: Int,
    ): BookItemSearchResult =
        BookItemSearchResult(
            bookProductId = bookProductId,
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
        expectedBookProductId: String,
        expectedPublisher: String,
        expectedAuthorNames: List<String>,
        expectedIsbn: String,
        expectedAvailableCount: Int,
        expectedTotalCount: Int,
    ) {
        val item = result[index]
        assertEquals(expectedBookProductId, item.bookProductId)
        assertEquals(expectedTitle, item.title)
        assertEquals(expectedPublisher, item.publisher)
        assertEquals(expectedAuthorNames, item.authorNames)
        assertEquals(expectedIsbn, item.isbn)
        assertEquals(expectedAvailableCount, item.availableCount)
        assertEquals(expectedTotalCount, item.totalCount)
    }
}
