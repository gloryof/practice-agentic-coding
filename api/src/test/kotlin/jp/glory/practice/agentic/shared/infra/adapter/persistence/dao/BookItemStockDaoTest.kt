package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookItemStockStatus
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookItemTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.PublisherTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.publisherTable
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import jp.glory.practice.agentic.shared.testinfra.UuidGenerator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BookItemStockDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: BookItemStockDao

    private val bookItems = Meta.bookItemTable
    private val bookProducts = Meta.bookProductTable
    private val publishers = Meta.publisherTable

    @Nested
    inner class CountByBookProductIds {
        @Test
        fun `given available and checked out stocks when count by product ids then returns available and total`() {
            val bookProductId = insertBookProduct("9780000000021")
            insertBookItemWithStock(bookProductId, BookItemStockStatus.AVAILABLE)
            insertBookItemWithStock(bookProductId, BookItemStockStatus.CHECKED_OUT)

            val result = sut.countByBookProductIds(listOf(bookProductId))

            assertEquals(1, result[bookProductId]?.availableCount)
            assertEquals(2, result[bookProductId]?.totalCount)
        }

        @Test
        fun `given empty ids when count by product ids then returns empty map`() {
            assertEquals(emptyMap(), sut.countByBookProductIds(emptyList()))
        }
    }

    private fun insertBookProduct(isbn: String): String {
        val publisherId = UuidGenerator.v7()
        val bookProductId = UuidGenerator.v7()

        komapperDatabase.runQuery {
            QueryDsl.insert(publishers).single(PublisherTable(id = publisherId, name = "技術書房", nameKana = "ぎじゅつしょぼう"))
        }
        komapperDatabase.runQuery {
            QueryDsl.insert(bookProducts).single(
                BookProductTable(
                    id = bookProductId,
                    title = "Kotlin",
                    titleKana = "ことりん",
                    publisherId = publisherId,
                    isbn = isbn,
                ),
            )
        }

        return bookProductId
    }

    private fun insertBookItemWithStock(
        bookProductId: String,
        status: BookItemStockStatus,
    ) {
        val bookItemId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(bookItems).single(BookItemTable(id = bookItemId, bookProductId = bookProductId))
        }
        val stockId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.executeScript(
                """
                INSERT INTO book_item_stocks (id, book_item_id, status)
                VALUES ('$stockId', '$bookItemId', '${status.name}'::book_item_stock_status)
                """.trimIndent(),
            )
        }
    }
}
