package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookItemStockStatus
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookItemTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.PublisherTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemStockTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookItemTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.libraryUserTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.publisherTable
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import jp.glory.practice.agentic.shared.testinfra.UuidGenerator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.komapper.core.UniqueConstraintException
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.bind
import org.komapper.core.dsl.query.getNotNull
import org.komapper.core.dsl.query.single
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReservationDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: ReservationDao

    private val libraryUsers = Meta.libraryUserTable
    private val publishers = Meta.publisherTable
    private val bookProducts = Meta.bookProductTable
    private val bookItems = Meta.bookItemTable
    private val bookItemStocks = Meta.bookItemStockTable

    @Nested
    inner class ReserveAvailableBookItem {
        @Test
        fun `given available stock when reserve available book item then updates status and returns book item id`() {
            val bookProductId = insertBookProduct()
            val bookItemId = insertBookItemWithStock(bookProductId, BookItemStockStatus.AVAILABLE)

            val result = sut.reserveAvailableBookItem(bookProductId)

            assertEquals(bookItemId, result)
            val stock = findStockByBookItemId(bookItemId)
            assertEquals(BookItemStockStatus.RESERVED, stock.status)
            assertEquals(1, stock.version)
        }

        @Test
        fun `given no available stock when reserve available book item then returns null`() {
            val bookProductId = insertBookProduct()
            insertBookItemWithStock(bookProductId, BookItemStockStatus.CHECKED_OUT)

            assertNull(sut.reserveAvailableBookItem(bookProductId))
        }
    }

    @Nested
    inner class InsertReservation {
        @Test
        fun `given reservation values when insert reservation then stores reservation`() {
            val libraryUserId = insertLibraryUser()
            val bookProductId = insertBookProduct()
            val bookItemId = insertBookItemWithStock(bookProductId, BookItemStockStatus.RESERVED)

            sut.insertReservation(
                id = "reservation-1",
                libraryUserId = libraryUserId,
                bookProductId = bookProductId,
                bookItemId = bookItemId,
                reservedAt = Instant.parse("2026-02-22T12:34:56Z"),
            )

            assertEquals(listOf(bookProductId), sut.findReservedBookProductIds(libraryUserId))
        }

        @Test
        fun `given duplicate book product reservation when insert reservation then database rejects it`() {
            val libraryUserId = insertLibraryUser()
            val bookProductId = insertBookProduct()
            val bookItemId1 = insertBookItemWithStock(bookProductId, BookItemStockStatus.RESERVED)
            val bookItemId2 = insertBookItemWithStock(bookProductId, BookItemStockStatus.RESERVED)
            sut.insertReservation(
                id = "reservation-1",
                libraryUserId = libraryUserId,
                bookProductId = bookProductId,
                bookItemId = bookItemId1,
                reservedAt = Instant.parse("2026-02-22T12:34:56Z"),
            )

            assertFailsWith<UniqueConstraintException> {
                sut.insertReservation(
                    id = "reservation-2",
                    libraryUserId = libraryUserId,
                    bookProductId = bookProductId,
                    bookItemId = bookItemId2,
                    reservedAt = Instant.parse("2026-02-22T12:35:56Z"),
                )
            }
        }
    }

    @Nested
    inner class FindBookProduct {
        @Test
        fun `given existing book product when find book product then returns title and isbn`() {
            val bookProductId = insertBookProduct()

            val result = sut.findBookProduct(bookProductId)

            assertNotNull(result)
            assertEquals(bookProductId, result.id)
            assertEquals("Kotlin入門", result.title)
            assertEquals("9780000000001", result.isbn)
        }
    }

    private fun insertLibraryUser(): String {
        val libraryUserId = "user-1"
        komapperDatabase.runQuery {
            QueryDsl.insert(libraryUsers).single(
                LibraryUserTable(
                    id = libraryUserId,
                    email = "user@example.com",
                    registeredAt = Instant.parse("2026-02-22T12:00:00Z"),
                ),
            )
        }
        return libraryUserId
    }

    private fun insertBookProduct(): String {
        val publisherId = "publisher-1"
        val bookProductId = "book-1"
        komapperDatabase.runQuery {
            QueryDsl.insert(publishers).single(PublisherTable(id = publisherId, name = "技術書房", nameKana = "ぎじゅつしょぼう"))
        }
        komapperDatabase.runQuery {
            QueryDsl.insert(bookProducts).single(
                BookProductTable(
                    id = bookProductId,
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherId = publisherId,
                    isbn = "9780000000001",
                ),
            )
        }
        return bookProductId
    }

    private fun insertBookItemWithStock(
        bookProductId: String,
        status: BookItemStockStatus,
    ): String {
        val bookItemId = UuidGenerator.v7()
        val stockId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(bookItems).single(BookItemTable(id = bookItemId, bookProductId = bookProductId))
        }
        komapperDatabase.runQuery {
            QueryDsl
                .executeTemplate(
                    """
                    INSERT INTO book_item_stocks (id, book_item_id, status)
                    VALUES (
                        /* stockId */'stock-id',
                        /* bookItemId */'item-id',
                        /* status */'AVAILABLE'::book_item_stock_status
                    )
                    """.trimIndent(),
                ).bind("stockId", stockId)
                .bind("bookItemId", bookItemId)
                .bind("status", status.name)
        }
        return bookItemId
    }

    private fun findStockByBookItemId(bookItemId: String): BookItemStockRow =
        komapperDatabase.runQuery {
            QueryDsl
                .fromTemplate(
                    """
                    SELECT status, version
                    FROM book_item_stocks
                    WHERE book_item_id = /* bookItemId */'item-id'
                    """.trimIndent(),
                ).bind("bookItemId", bookItemId)
                .select { row ->
                    BookItemStockRow(
                        status = BookItemStockStatus.valueOf(row.getNotNull("status")),
                        version = row.getNotNull("version"),
                    )
                }.single()
        }
}

private data class BookItemStockRow(
    val status: BookItemStockStatus,
    val version: Int,
)
