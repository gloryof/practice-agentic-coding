package jp.glory.practice.agentic.catalog.query.web

import jp.glory.practice.agentic.shared.web.ValidationApiException
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BookItemSearchRequestValidatorTest {
    private val sut = BookItemSearchRequestValidator()

    @Nested
    inner class Title {
        @Test
        fun `accepts title and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        title = " Kotlin ",
                    ),
                )

            assertEquals("Kotlin", input.title)
        }

        @Test
        fun `rejects blank title`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(title = " "))
                }

            assertDetail(exception, "title", "required")
        }

        @Test
        fun `accepts title exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        title = "Kotlin",
                        titleExact = "true",
                    ),
                )

            assertTrue(input.titleExact)
        }

        @Test
        fun `rejects title exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleExact = "true"))
                }

            assertDetail(exception, "title_exact", "requires_value")
        }

        @Test
        fun `rejects title exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            title = "Kotlin",
                            titleExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "title_exact", "must_be_true")
        }
    }

    @Nested
    inner class TitleKana {
        @Test
        fun `accepts title kana and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        titleKana = " ことりん ",
                    ),
                )

            assertEquals("ことりん", input.titleKana)
        }

        @Test
        fun `rejects blank title kana`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleKana = " "))
                }

            assertDetail(exception, "title_kana", "required")
        }

        @Test
        fun `accepts title kana exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        titleKana = "ことりん",
                        titleKanaExact = "true",
                    ),
                )

            assertTrue(input.titleKanaExact)
        }

        @Test
        fun `rejects title kana exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleKanaExact = "true"))
                }

            assertDetail(exception, "title_kana_exact", "requires_value")
        }

        @Test
        fun `rejects title kana exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            titleKana = "ことりん",
                            titleKanaExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "title_kana_exact", "must_be_true")
        }
    }

    @Nested
    inner class Publisher {
        @Test
        fun `accepts publisher and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisher = " 技術書房 ",
                    ),
                )

            assertEquals("技術書房", input.publisher)
        }

        @Test
        fun `rejects blank publisher`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisher = " "))
                }

            assertDetail(exception, "publisher", "required")
        }

        @Test
        fun `accepts publisher exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisher = "技術書房",
                        publisherExact = "true",
                    ),
                )

            assertTrue(input.publisherExact)
        }

        @Test
        fun `rejects publisher exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherExact = "true"))
                }

            assertDetail(exception, "publisher_exact", "requires_value")
        }

        @Test
        fun `rejects publisher exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            publisher = "技術書房",
                            publisherExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "publisher_exact", "must_be_true")
        }
    }

    @Nested
    inner class PublisherKana {
        @Test
        fun `accepts publisher kana and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisherKana = " ぎじゅつしょぼう ",
                    ),
                )

            assertEquals("ぎじゅつしょぼう", input.publisherKana)
        }

        @Test
        fun `rejects blank publisher kana`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherKana = " "))
                }

            assertDetail(exception, "publisher_kana", "required")
        }

        @Test
        fun `accepts publisher kana exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisherKana = "ぎじゅつしょぼう",
                        publisherKanaExact = "true",
                    ),
                )

            assertTrue(input.publisherKanaExact)
        }

        @Test
        fun `rejects publisher kana exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherKanaExact = "true"))
                }

            assertDetail(exception, "publisher_kana_exact", "requires_value")
        }

        @Test
        fun `rejects publisher kana exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            publisherKana = "ぎじゅつしょぼう",
                            publisherKanaExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "publisher_kana_exact", "must_be_true")
        }
    }

    @Nested
    inner class AuthorName {
        @Test
        fun `accepts author name and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorName = " 山田太郎 ",
                    ),
                )

            assertEquals("山田太郎", input.authorName)
        }

        @Test
        fun `rejects blank author name`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorName = " "))
                }

            assertDetail(exception, "author_name", "required")
        }

        @Test
        fun `accepts author exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorName = "山田太郎",
                        authorExact = "true",
                    ),
                )

            assertTrue(input.authorExact)
        }

        @Test
        fun `rejects author exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorExact = "true"))
                }

            assertDetail(exception, "author_exact", "requires_value")
        }

        @Test
        fun `rejects author exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            authorName = "山田太郎",
                            authorExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "author_exact", "must_be_true")
        }
    }

    @Nested
    inner class AuthorNameKana {
        @Test
        fun `accepts author name kana and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorNameKana = " やまだたろう ",
                    ),
                )

            assertEquals("やまだたろう", input.authorNameKana)
        }

        @Test
        fun `rejects blank author name kana`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorNameKana = " "))
                }

            assertDetail(exception, "author_name_kana", "required")
        }

        @Test
        fun `accepts author kana exact flag when true`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorNameKana = "やまだたろう",
                        authorKanaExact = "true",
                    ),
                )

            assertTrue(input.authorKanaExact)
        }

        @Test
        fun `rejects author kana exact when missing value`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorKanaExact = "true"))
                }

            assertDetail(exception, "author_kana_exact", "requires_value")
        }

        @Test
        fun `rejects author kana exact when not true`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            authorNameKana = "やまだたろう",
                            authorKanaExact = "false",
                        ),
                    )
                }

            assertDetail(exception, "author_kana_exact", "must_be_true")
        }
    }

    @Nested
    inner class Isbn {
        @Test
        fun `accepts isbn and trims`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        isbn = " 9780000000001 ",
                    ),
                )

            assertEquals("9780000000001", input.isbn)
        }

        @Test
        fun `rejects blank isbn`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(isbn = " "))
                }

            assertDetail(exception, "isbn", "required")
        }
    }

    @Nested
    inner class Criteria {
        @Test
        fun `rejects when all criteria missing`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest())
                }

            assertDetail(exception, "criteria", "required")
        }

        @Test
        fun `accepts when one criteria provided`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        title = "Kotlin",
                    ),
                )

            assertEquals("Kotlin", input.title)
        }

        @Test
        fun `returns multiple details when errors overlap`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(
                        BookItemSearchRequest(
                            title = " ",
                            titleExact = "true",
                        ),
                    )
                }

            assertDetail(exception, "title", "required")
            assertDetail(exception, "title_exact", "requires_value")
            assertDetail(exception, "criteria", "required")
        }
    }

    private fun validate(request: BookItemSearchRequest) = sut.validateAndConvert(request)

    private fun assertDetail(
        exception: ValidationApiException,
        field: String,
        reason: String,
    ) {
        val details = exception.details.map { it.field to it.reason }
        assertTrue(details.contains(field to reason))
    }
}
