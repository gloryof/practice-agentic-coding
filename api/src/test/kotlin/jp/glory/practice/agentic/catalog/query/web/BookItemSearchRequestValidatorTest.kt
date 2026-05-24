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
        fun `given value with spaces when validate title then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        title = " Kotlin ",
                    ),
                )

            assertEquals("Kotlin", input.title)
        }

        @Test
        fun `given blank title when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(title = " "))
                }

            assertDetail(exception, "title", "required")
        }

        @Test
        fun `given title and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without title when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleExact = "true"))
                }

            assertDetail(exception, "title_exact", "requires_value")
        }

        @Test
        fun `given title and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate title kana then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        titleKana = " ことりん ",
                    ),
                )

            assertEquals("ことりん", input.titleKana)
        }

        @Test
        fun `given blank title kana when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleKana = " "))
                }

            assertDetail(exception, "title_kana", "required")
        }

        @Test
        fun `given title kana and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without title kana when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(titleKanaExact = "true"))
                }

            assertDetail(exception, "title_kana_exact", "requires_value")
        }

        @Test
        fun `given title kana and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate publisher then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisher = " 技術書房 ",
                    ),
                )

            assertEquals("技術書房", input.publisher)
        }

        @Test
        fun `given blank publisher when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisher = " "))
                }

            assertDetail(exception, "publisher", "required")
        }

        @Test
        fun `given publisher and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without publisher when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherExact = "true"))
                }

            assertDetail(exception, "publisher_exact", "requires_value")
        }

        @Test
        fun `given publisher and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate publisher kana then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        publisherKana = " ぎじゅつしょぼう ",
                    ),
                )

            assertEquals("ぎじゅつしょぼう", input.publisherKana)
        }

        @Test
        fun `given blank publisher kana when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherKana = " "))
                }

            assertDetail(exception, "publisher_kana", "required")
        }

        @Test
        fun `given publisher kana and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without publisher kana when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(publisherKanaExact = "true"))
                }

            assertDetail(exception, "publisher_kana_exact", "requires_value")
        }

        @Test
        fun `given publisher kana and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate author name then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorName = " 山田太郎 ",
                    ),
                )

            assertEquals("山田太郎", input.authorName)
        }

        @Test
        fun `given blank author name when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorName = " "))
                }

            assertDetail(exception, "author_name", "required")
        }

        @Test
        fun `given author and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without author when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorExact = "true"))
                }

            assertDetail(exception, "author_exact", "requires_value")
        }

        @Test
        fun `given author and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate author name kana then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        authorNameKana = " やまだたろう ",
                    ),
                )

            assertEquals("やまだたろう", input.authorNameKana)
        }

        @Test
        fun `given blank author name kana when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorNameKana = " "))
                }

            assertDetail(exception, "author_name_kana", "required")
        }

        @Test
        fun `given author kana and exact flag true when validate then sets exact flag`() {
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
        fun `given exact flag without author kana when validate then returns requires value error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest(authorKanaExact = "true"))
                }

            assertDetail(exception, "author_kana_exact", "requires_value")
        }

        @Test
        fun `given author kana and exact flag false when validate then returns must be true error`() {
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
        fun `given value with spaces when validate isbn then trims value`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        isbn = " 9780000000001 ",
                    ),
                )

            assertEquals("9780000000001", input.isbn)
        }

        @Test
        fun `given blank isbn when validate then returns validation error`() {
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
        fun `given missing all criteria when validate then returns validation error`() {
            val exception =
                assertFailsWith<ValidationApiException> {
                    validate(BookItemSearchRequest())
                }

            assertDetail(exception, "criteria", "required")
        }

        @Test
        fun `given one criteria when validate then returns input`() {
            val input =
                validate(
                    BookItemSearchRequest(
                        title = "Kotlin",
                    ),
                )

            assertEquals("Kotlin", input.title)
        }

        @Test
        fun `given overlapped invalid criteria when validate then returns multiple details`() {
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
