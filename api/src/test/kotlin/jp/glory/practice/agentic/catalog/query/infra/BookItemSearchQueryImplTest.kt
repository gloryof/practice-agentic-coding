package jp.glory.practice.agentic.catalog.query.infra

import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.shared.testinfra.PostgreSqlTestBase
import jp.glory.practice.agentic.shared.testinfra.UuidGenerator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BookItemSearchQueryImplTest : PostgreSqlTestBase() {
    @Autowired
    private lateinit var sut: BookItemSearchQueryImpl

    @Nested
    inner class Title {
        @Test
        fun `filters by title`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "Kotlin出版",
                publisherKana = "ことりんしゅっぱん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(title = "Kotlin"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by title exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Kotlin入門改",
                titleKana = "ことりんにゅうもんかい",
                publisherName = "技術書房別館",
                publisherKana = "ぎじゅつしょぼうべっかん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(title = "Kotlin入門", titleExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class TitleKana {
        @Test
        fun `filters by title kana`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "ことりん出版",
                publisherKana = "ことりんしゅっぱん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(titleKana = "ことりん"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by title kana exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Kotlin入門改",
                titleKana = "ことりんにゅうもんかい",
                publisherName = "技術書房別館",
                publisherKana = "ぎじゅつしょぼうべっかん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(titleKana = "ことりんにゅうもん", titleKanaExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class Publisher {
        @Test
        fun `filters by publisher`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "技術書房の物語",
                titleKana = "ぎじゅつしょぼうものがたり",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(publisher = "技術"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by publisher exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "技術書房別館",
                publisherKana = "ぎじゅつしょぼうべっかん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(publisher = "技術書房", publisherExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class PublisherKana {
        @Test
        fun `filters by publisher kana`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "ぎじゅつしょぼうものがたり",
                titleKana = "ぎじゅつしょぼうものがたり",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(publisherKana = "ぎじゅつ"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by publisher kana exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "技術書房別館",
                publisherKana = "ぎじゅつしょぼうべっかん",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(publisherKana = "ぎじゅつしょぼう", publisherKanaExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class AuthorName {
        @Test
        fun `filters by author name`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "山田物語",
                titleKana = "やまだものがたり",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(authorName = "山田"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by author exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "山田太郎子",
                authorKana = "やまだたろうこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(authorName = "山田太郎", authorExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class AuthorNameKana {
        @Test
        fun `filters by author name kana`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "やまだたろう物語",
                titleKana = "やまだたろうものがたり",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(authorNameKana = "やまだ"))

            assertBookIds(result, setOf(targetId))
        }

        @Test
        fun `filters by author kana exact`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "山田太郎子",
                authorKana = "やまだたろうこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(authorNameKana = "やまだたろう", authorKanaExact = true))

            assertBookIds(result, setOf(targetId))
        }
    }

    @Nested
    inner class Isbn {
        @Test
        fun `filters by isbn`() {
            val targetId =
                insertBookWithAuthor(
                    title = "Kotlin入門",
                    titleKana = "ことりんにゅうもん",
                    publisherName = "技術書房",
                    publisherKana = "ぎじゅつしょぼう",
                    authorName = "山田太郎",
                    authorKana = "やまだたろう",
                    isbn = "9780000000001",
                )
            insertBookWithAuthor(
                title = "Java実践",
                titleKana = "じゃばじっせん",
                publisherName = "小説社",
                publisherKana = "しょうせつしゃ",
                authorName = "佐藤花子",
                authorKana = "さとうはなこ",
                isbn = "9780000000002",
            )

            val result = sut.search(input(isbn = "9780000000001"))

            assertBookIds(result, setOf(targetId))
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

    private fun assertBookIds(
        results: List<jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchResult>,
        expectedIds: Set<String>,
    ) {
        assertEquals(expectedIds, results.map { it.bookItemId }.toSet())
    }

    private fun insertBookWithAuthor(
        title: String,
        titleKana: String,
        publisherName: String,
        publisherKana: String,
        authorName: String,
        authorKana: String,
        isbn: String,
    ): String {
        val publisherId = insertPublisher(publisherName, publisherKana)
        val authorId = insertAuthor(authorName, authorKana)
        val bookId = insertBookItem(title, titleKana, publisherId, isbn)
        insertBookItemAuthor(bookId, authorId)
        return bookId
    }

    private fun insertPublisher(
        name: String,
        nameKana: String,
    ): String {
        val id = UuidGenerator.v7()
        jdbcTemplate.update(
            "INSERT INTO publishers (id, name, name_kana) VALUES (?, ?, ?)",
            id,
            name,
            nameKana,
        )
        return id
    }

    private fun insertAuthor(
        name: String,
        nameKana: String,
    ): String {
        val id = UuidGenerator.v7()
        jdbcTemplate.update(
            "INSERT INTO authors (id, name, name_kana) VALUES (?, ?, ?)",
            id,
            name,
            nameKana,
        )
        return id
    }

    private fun insertBookItem(
        title: String,
        titleKana: String,
        publisherId: String,
        isbn: String,
    ): String {
        val id = UuidGenerator.v7()
        jdbcTemplate.update(
            "INSERT INTO book_items (id, title, title_kana, publisher_id, isbn) VALUES (?, ?, ?, ?, ?)",
            id,
            title,
            titleKana,
            publisherId,
            isbn,
        )
        return id
    }

    private fun insertBookItemAuthor(
        bookItemId: String,
        authorId: String,
    ) {
        jdbcTemplate.update(
            "INSERT INTO book_item_authors (book_item_id, author_id) VALUES (?, ?)",
            bookItemId,
            authorId,
        )
    }
}
