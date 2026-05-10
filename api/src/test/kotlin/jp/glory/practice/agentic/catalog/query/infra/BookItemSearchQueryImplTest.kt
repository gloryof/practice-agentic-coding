package jp.glory.practice.agentic.catalog.query.infra

import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.AuthorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.BookItemStockStatus
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.BookItemTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.BookProductAuthorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.BookProductTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.PublisherTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.authorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookItemTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookProductAuthorTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.bookProductTable
import jp.glory.practice.agentic.catalog.query.infra.adapter.persistence.publisherTable
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import jp.glory.practice.agentic.shared.testinfra.UuidGenerator
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BookItemSearchQueryImplTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: BookItemSearchQueryImpl

    private val bookItems = Meta.bookItemTable.clone(table = "book_items")
    private val bookProducts = Meta.bookProductTable.clone(table = "book_products")
    private val publishers = Meta.publisherTable.clone(table = "publishers")
    private val authors = Meta.authorTable.clone(table = "authors")
    private val bookProductAuthors = Meta.bookProductAuthorTable.clone(table = "book_product_authors")

    @Test
    fun `filters by title and resolves available when one stock is available`() {
        val kotlin =
            insertBookProductWithItems(
                title = "Kotlin入門",
                titleKana = "ことりんにゅうもん",
                publisherName = "技術書房",
                publisherKana = "ぎじゅつしょぼう",
                authorName = "山田太郎",
                authorKana = "やまだたろう",
                isbn = "9780000000001",
                stockStatuses = listOf(BookItemStockStatus.CHECKED_OUT, BookItemStockStatus.AVAILABLE),
            )
        insertBookProductWithItems(
            title = "Java実践",
            titleKana = "じゃばじっせん",
            publisherName = "技術書房",
            publisherKana = "ぎじゅつしょぼう",
            authorName = "佐藤花子",
            authorKana = "さとうはなこ",
            isbn = "9780000000002",
            stockStatuses = listOf(BookItemStockStatus.CHECKED_OUT),
        )

        val result = sut.search(input(title = "Kotlin"))

        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("Kotlin入門", item.title)
        assertEquals("9780000000001", item.isbn)
        assertEquals(listOf("山田太郎"), item.authorNames)
        assertEquals(1, item.availableCount)
        assertEquals(2, item.totalCount)
        assertEquals(kotlin.bookProductId, findBookProductIdByIsbn(item.isbn))
    }

    @Test
    fun `returns zero available count when all item stocks are checked out`() {
        val inserted =
            insertBookProductWithItems(
                title = "Kotlin実践",
                titleKana = "ことりんじっせん",
                publisherName = "技術書房",
                publisherKana = "ぎじゅつしょぼう",
                authorName = "山田太郎",
                authorKana = "やまだたろう",
                isbn = "9780000000003",
                stockStatuses = listOf(BookItemStockStatus.CHECKED_OUT, BookItemStockStatus.CHECKED_OUT),
            )

        val result = sut.search(input(title = "Kotlin実践", titleExact = true))

        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("Kotlin実践", item.title)
        assertEquals("技術書房", item.publisher)
        assertEquals(listOf("山田太郎"), item.authorNames)
        assertEquals(inserted.isbn, item.isbn)
        assertEquals(0, item.availableCount)
        assertEquals(2, item.totalCount)
    }

    @Test
    fun `filters by author name kana`() {
        val kotlin =
            insertBookProductWithItems(
                title = "Kotlin入門",
                titleKana = "ことりんにゅうもん",
                publisherName = "技術書房",
                publisherKana = "ぎじゅつしょぼう",
                authorName = "山田太郎",
                authorKana = "やまだたろう",
                isbn = "9780000000001",
                stockStatuses = listOf(BookItemStockStatus.AVAILABLE),
            )
        insertBookProductWithItems(
            title = "Java実践",
            titleKana = "じゃばじっせん",
            publisherName = "技術書房",
            publisherKana = "ぎじゅつしょぼう",
            authorName = "佐藤花子",
            authorKana = "さとうはなこ",
            isbn = "9780000000002",
            stockStatuses = listOf(BookItemStockStatus.AVAILABLE),
        )

        val result = sut.search(input(authorNameKana = "やまだ"))

        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("Kotlin入門", item.title)
        assertEquals("技術書房", item.publisher)
        assertEquals(listOf("山田太郎"), item.authorNames)
        assertEquals(kotlin.isbn, item.isbn)
        assertEquals(1, item.availableCount)
        assertEquals(1, item.totalCount)
    }

    private fun insertBookProductWithItems(
        title: String,
        titleKana: String,
        publisherName: String,
        publisherKana: String,
        authorName: String,
        authorKana: String,
        isbn: String,
        stockStatuses: List<BookItemStockStatus>,
    ): InsertedBookProduct {
        val publisherId = insertPublisher(publisherName, publisherKana)
        val authorId = insertAuthor(authorName, authorKana)
        val bookProductId = UuidGenerator.v7()

        komapperDatabase.runQuery {
            QueryDsl.insert(bookProducts).single(
                BookProductTable(
                    id = bookProductId,
                    title = title,
                    titleKana = titleKana,
                    publisherId = publisherId,
                    isbn = isbn,
                ),
            )
        }
        komapperDatabase.runQuery {
            QueryDsl.insert(bookProductAuthors).single(
                BookProductAuthorTable(bookProductId = bookProductId, authorId = authorId),
            )
        }

        stockStatuses.forEach { status ->
            val bookItemId = UuidGenerator.v7()
            komapperDatabase.runQuery {
                QueryDsl.insert(bookItems).single(BookItemTable(id = bookItemId, bookProductId = bookProductId))
            }
            insertBookItemStock(bookItemId, status)
        }

        return InsertedBookProduct(bookProductId = bookProductId, isbn = isbn)
    }

    private fun insertPublisher(
        name: String,
        nameKana: String,
    ): String {
        val publisherId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(publishers).single(PublisherTable(id = publisherId, name = name, nameKana = nameKana))
        }
        return publisherId
    }

    private fun insertAuthor(
        name: String,
        nameKana: String,
    ): String {
        val authorId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(authors).single(AuthorTable(id = authorId, name = name, nameKana = nameKana))
        }
        return authorId
    }

    private fun insertBookItemStock(
        bookItemId: String,
        status: BookItemStockStatus,
    ) {
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

    private fun findBookProductIdByIsbn(isbn: String): String {
        val row =
            jdbcTemplate.queryForMap(
                "SELECT id FROM book_products WHERE isbn = ?",
                isbn,
            )
        return row["id"] as String
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

private data class InsertedBookProduct(
    val bookProductId: String,
    val isbn: String,
)
