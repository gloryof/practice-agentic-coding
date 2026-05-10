package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.AuthorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookProductAuthorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.BookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.PublisherTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.authorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductAuthorTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.bookProductTable
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.publisherTable
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import jp.glory.practice.agentic.shared.testinfra.UuidGenerator
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BookProductAuthorDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: BookProductAuthorDao

    private val bookProducts = Meta.bookProductTable.clone(table = "book_products")
    private val publishers = Meta.publisherTable.clone(table = "publishers")
    private val authors = Meta.authorTable.clone(table = "authors")
    private val bookProductAuthors = Meta.bookProductAuthorTable.clone(table = "book_product_authors")

    @Test
    fun `findAuthorNamesByBookProductIds groups and sorts author names`() {
        val publisherId = insertPublisher()
        val bookProductId = insertBookProduct(publisherId, "9780000000011")
        val authorA = insertAuthor("佐藤花子")
        val authorB = insertAuthor("山田太郎")
        komapperDatabase.runQuery {
            QueryDsl.insert(bookProductAuthors).single(BookProductAuthorTable(bookProductId = bookProductId, authorId = authorB))
        }
        komapperDatabase.runQuery {
            QueryDsl.insert(bookProductAuthors).single(BookProductAuthorTable(bookProductId = bookProductId, authorId = authorA))
        }

        val result = sut.findAuthorNamesByBookProductIds(listOf(bookProductId))

        assertEquals(listOf("佐藤花子", "山田太郎"), result[bookProductId])
    }

    @Test
    fun `findAuthorNamesByBookProductIds returns empty map for empty ids`() {
        assertEquals(emptyMap(), sut.findAuthorNamesByBookProductIds(emptyList()))
    }

    private fun insertPublisher(): String {
        val publisherId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(publishers).single(PublisherTable(id = publisherId, name = "技術書房", nameKana = "ぎじゅつしょぼう"))
        }
        return publisherId
    }

    private fun insertBookProduct(
        publisherId: String,
        isbn: String,
    ): String {
        val bookProductId = UuidGenerator.v7()
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

    private fun insertAuthor(name: String): String {
        val authorId = UuidGenerator.v7()
        komapperDatabase.runQuery {
            QueryDsl.insert(authors).single(AuthorTable(id = authorId, name = name, nameKana = "かな"))
        }
        return authorId
    }
}
