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
    fun `returns author names as list`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)
        val input = defaultInput

        every { query.search(input) } returns
            listOf(
                BookItemSearchResult(
                    bookItemId = "book-1",
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎", "佐藤花子"),
                    isbn = "9780000000001",
                ),
            )

        val result = useCase.search(input)

        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("Kotlin入門", item.title)
        assertEquals("技術書房", item.publisher)
        assertEquals(listOf("山田太郎", "佐藤花子"), item.authorNames)
        assertEquals("9780000000001", item.isbn)
    }

    @Test
    fun `returns empty list when query has no results`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)
        val input = defaultInput

        every { query.search(input) } returns emptyList()

        val result = useCase.search(input)

        assertEquals(0, result.size)
    }

    @Test
    fun `returns multiple items when query has multiple results`() {
        val query = mockk<BookItemSearchQuery>()
        val useCase = BookItemSearchUseCase(query)
        val input = defaultInput

        every { query.search(input) } returns
            listOf(
                BookItemSearchResult(
                    bookItemId = "book-1",
                    title = "Kotlin入門",
                    publisher = "技術書房",
                    authorNames = listOf("山田太郎"),
                    isbn = "9780000000001",
                ),
                BookItemSearchResult(
                    bookItemId = "book-2",
                    title = "Kotlin実践",
                    publisher = "技術書房",
                    authorNames = listOf("佐藤花子"),
                    isbn = "9780000000002",
                ),
            )

        val result = useCase.search(input)

        assertEquals(2, result.size)
        val titles = result.map { it.title }.toSet()
        assertEquals(setOf("Kotlin入門", "Kotlin実践"), titles)
    }
}
