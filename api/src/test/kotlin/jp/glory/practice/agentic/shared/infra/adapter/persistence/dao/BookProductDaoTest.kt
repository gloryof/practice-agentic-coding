package jp.glory.practice.agentic.shared.infra.adapter.persistence.dao

import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BookProductDaoTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: BookProductDao

    private val bookProducts = Meta.bookProductTable
    private val publishers = Meta.publisherTable
    private val authors = Meta.authorTable
    private val bookProductAuthors = Meta.bookProductAuthorTable

    @Nested
    inner class FindBySearchInput {
        @Test
        fun `given partial title query when find by search input then filters by partial match`() {
            insertBookData("Kotlin入門", "ことりんにゅうもん", "技術書房", "ぎじゅつしょぼう", "山田太郎", "やまだたろう", "9780000000001")
            insertBookData("Java実践", "じゃばじっせん", "技術書房", "ぎじゅつしょぼう", "佐藤花子", "さとうはなこ", "9780000000002")

            val result = sut.findBySearchInput(input(title = "kotlin"))

            assertEquals(1, result.size)
            assertEquals("Kotlin入門", result.first().title)
        }

        @Test
        fun `given author kana query when find by search input then filters by author kana`() {
            insertBookData("Kotlin入門", "ことりんにゅうもん", "技術書房", "ぎじゅつしょぼう", "山田太郎", "やまだたろう", "9780000000003")
            insertBookData("Java実践", "じゃばじっせん", "技術書房", "ぎじゅつしょぼう", "佐藤花子", "さとうはなこ", "9780000000004")

            val result = sut.findBySearchInput(input(authorNameKana = "やまだ"))

            assertEquals(1, result.size)
            assertEquals("9780000000003", result.first().isbn)
        }

        @Test
        fun `given exact title query when find by search input then filters by exact title`() {
            insertBookData("Kotlin実践", "ことりんじっせん", "技術書房", "ぎじゅつしょぼう", "山田太郎", "やまだたろう", "9780000000005")
            insertBookData("Kotlin実践ガイド", "ことりんじっせんがいど", "技術書房", "ぎじゅつしょぼう", "山田太郎", "やまだたろう", "9780000000006")

            val result = sut.findBySearchInput(input(title = "Kotlin実践", titleExact = true))

            assertEquals(1, result.size)
            assertEquals("9780000000005", result.first().isbn)
        }
    }

    private fun insertBookData(
        title: String,
        titleKana: String,
        publisherName: String,
        publisherKana: String,
        authorName: String,
        authorKana: String,
        isbn: String,
    ): String {
        val publisherId = UuidGenerator.v7()
        val authorId = UuidGenerator.v7()
        val bookProductId = UuidGenerator.v7()

        komapperDatabase.runQuery {
            QueryDsl.insert(publishers).single(PublisherTable(id = publisherId, name = publisherName, nameKana = publisherKana))
        }
        komapperDatabase.runQuery {
            QueryDsl.insert(authors).single(AuthorTable(id = authorId, name = authorName, nameKana = authorKana))
        }
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
            QueryDsl.insert(bookProductAuthors).single(BookProductAuthorTable(bookProductId = bookProductId, authorId = authorId))
        }

        return bookProductId
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
