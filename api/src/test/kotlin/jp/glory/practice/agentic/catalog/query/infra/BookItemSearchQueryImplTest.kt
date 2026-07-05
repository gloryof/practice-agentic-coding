package jp.glory.practice.agentic.catalog.query.infra

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookItemStockDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookProductAuthorDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookProductDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.BookProductRow
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.StockCount
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BookItemSearchQueryImplTest {
    @Nested
    inner class Search {
        @Test
        fun `given empty dao results when search then returns empty list`() {
            val bookProductDao = mockk<BookProductDao>()
            val bookProductAuthorDao = mockk<BookProductAuthorDao>()
            val bookItemStockDao = mockk<BookItemStockDao>()
            val sut = BookItemSearchQueryImpl(bookProductDao, bookProductAuthorDao, bookItemStockDao)
            val input = input(title = "Kotlin")

            every { bookProductDao.findBySearchInput(input) } returns emptyList()

            val result = sut.search(input)

            assertEquals(emptyList(), result)
            verify(exactly = 1) { bookProductDao.findBySearchInput(input) }
            verify(exactly = 0) { bookProductAuthorDao.findAuthorNamesByBookProductIds(any()) }
            verify(exactly = 0) { bookItemStockDao.countByBookProductIds(any()) }
        }

        @Test
        fun `given dao results when search then maps to query result`() {
            val bookProductDao = mockk<BookProductDao>()
            val bookProductAuthorDao = mockk<BookProductAuthorDao>()
            val bookItemStockDao = mockk<BookItemStockDao>()
            val sut = BookItemSearchQueryImpl(bookProductDao, bookProductAuthorDao, bookItemStockDao)
            val input = input(authorNameKana = "やまだ")

            val rows =
                listOf(
                    BookProductRow(id = "book-1", title = "Kotlin入門", isbn = "9780000000001", publisher = "技術書房"),
                    BookProductRow(id = "book-2", title = "Java実践", isbn = "9780000000002", publisher = "技術書房"),
                )
            every { bookProductDao.findBySearchInput(input) } returns rows
            every { bookProductAuthorDao.findAuthorNamesByBookProductIds(listOf("book-1", "book-2")) } returns
                mapOf("book-1" to listOf("山田太郎"), "book-2" to listOf("佐藤花子"))
            every { bookItemStockDao.countByBookProductIds(listOf("book-1", "book-2")) } returns
                mapOf(
                    "book-1" to StockCount(availableCount = 1, totalCount = 2),
                    "book-2" to StockCount(availableCount = 0, totalCount = 1),
                )

            val result = sut.search(input)

            assertEquals(2, result.size)
            assertEquals("book-1", result[0].bookProductId)
            assertEquals("Kotlin入門", result[0].title)
            assertEquals("技術書房", result[0].publisher)
            assertEquals(listOf("山田太郎"), result[0].authorNames)
            assertEquals("9780000000001", result[0].isbn)
            assertEquals(1, result[0].availableCount)
            assertEquals(2, result[0].totalCount)

            assertEquals("book-2", result[1].bookProductId)
            assertEquals("Java実践", result[1].title)
            assertEquals(listOf("佐藤花子"), result[1].authorNames)
            assertEquals(0, result[1].availableCount)
            assertEquals(1, result[1].totalCount)
        }

        @Test
        fun `given missing stock counts when search then uses zero as default counts`() {
            val bookProductDao = mockk<BookProductDao>()
            val bookProductAuthorDao = mockk<BookProductAuthorDao>()
            val bookItemStockDao = mockk<BookItemStockDao>()
            val sut = BookItemSearchQueryImpl(bookProductDao, bookProductAuthorDao, bookItemStockDao)
            val input = input(isbn = "9780000000003")

            every { bookProductDao.findBySearchInput(input) } returns
                listOf(BookProductRow(id = "book-3", title = "DDD", isbn = "9780000000003", publisher = "青空社"))
            every { bookProductAuthorDao.findAuthorNamesByBookProductIds(listOf("book-3")) } returns emptyMap()
            every { bookItemStockDao.countByBookProductIds(listOf("book-3")) } returns emptyMap()

            val result = sut.search(input)

            assertEquals(1, result.size)
            assertEquals("book-3", result[0].bookProductId)
            assertEquals(0, result[0].availableCount)
            assertEquals(0, result[0].totalCount)
        }
    }

    private fun input(
        title: String? = null,
        titleExact: Boolean = false,
        titleKana: String? = null,
        titleKanaExact: Boolean = false,
        publisher: String? = null,
        publisherExact: Boolean = false,
        publisherKana: String? = null,
        publisherKanaExact: Boolean = false,
        authorName: String? = null,
        authorExact: Boolean = false,
        authorNameKana: String? = null,
        authorKanaExact: Boolean = false,
        isbn: String? = null,
    ): BookItemSearchInput =
        BookItemSearchInput(
            title = title,
            titleExact = titleExact,
            titleKana = titleKana,
            titleKanaExact = titleKanaExact,
            publisher = publisher,
            publisherExact = publisherExact,
            publisherKana = publisherKana,
            publisherKanaExact = publisherKanaExact,
            authorName = authorName,
            authorExact = authorExact,
            authorNameKana = authorNameKana,
            authorKanaExact = authorKanaExact,
            isbn = isbn,
        )
}
