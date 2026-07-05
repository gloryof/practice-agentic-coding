package jp.glory.practice.agentic.catalog.query.web

import com.fasterxml.jackson.annotation.JsonProperty
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchResults
import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/book-items")
class BookItemSearchController(
    private val validator: BookItemSearchRequestValidator,
    private val useCase: BookItemSearchUseCase,
    private val authenticator: AccessTokenAuthenticator,
) {
    @GetMapping
    fun search(
        @RequestHeader("Authorization", required = false) authorization: String?,
        @RequestParam(required = false) title: String?,
        @RequestParam(name = "title_kana", required = false) titleKana: String?,
        @RequestParam(required = false) publisher: String?,
        @RequestParam(name = "publisher_kana", required = false) publisherKana: String?,
        @RequestParam(name = "author_name", required = false) authorName: String?,
        @RequestParam(name = "author_name_kana", required = false) authorNameKana: String?,
        @RequestParam(required = false) isbn: String?,
        @RequestParam(name = "title_exact", required = false) titleExact: String?,
        @RequestParam(name = "title_kana_exact", required = false) titleKanaExact: String?,
        @RequestParam(name = "publisher_exact", required = false) publisherExact: String?,
        @RequestParam(name = "publisher_kana_exact", required = false) publisherKanaExact: String?,
        @RequestParam(name = "author_exact", required = false) authorExact: String?,
        @RequestParam(name = "author_kana_exact", required = false) authorKanaExact: String?,
    ): BookItemSearchResponse {
        authenticator.requireValidToken(authorization)
        val input =
            validator.validateAndConvert(
                buildRequest(
                    title = title,
                    titleKana = titleKana,
                    publisher = publisher,
                    publisherKana = publisherKana,
                    authorName = authorName,
                    authorNameKana = authorNameKana,
                    isbn = isbn,
                    titleExact = titleExact,
                    titleKanaExact = titleKanaExact,
                    publisherExact = publisherExact,
                    publisherKanaExact = publisherKanaExact,
                    authorExact = authorExact,
                    authorKanaExact = authorKanaExact,
                ),
            )
        return BookItemSearchResponse(items = toItems(useCase.search(input)))
    }

    private fun buildRequest(
        title: String?,
        titleKana: String?,
        publisher: String?,
        publisherKana: String?,
        authorName: String?,
        authorNameKana: String?,
        isbn: String?,
        titleExact: String?,
        titleKanaExact: String?,
        publisherExact: String?,
        publisherKanaExact: String?,
        authorExact: String?,
        authorKanaExact: String?,
    ): BookItemSearchRequest =
        BookItemSearchRequest(
            title = title,
            titleKana = titleKana,
            publisher = publisher,
            publisherKana = publisherKana,
            authorName = authorName,
            authorNameKana = authorNameKana,
            isbn = isbn,
            titleExact = titleExact,
            titleKanaExact = titleKanaExact,
            publisherExact = publisherExact,
            publisherKanaExact = publisherKanaExact,
            authorExact = authorExact,
            authorKanaExact = authorKanaExact,
        )

    private fun toItems(results: List<BookItemSearchResults>): List<BookItemSearchItem> =
        results.map {
            BookItemSearchItem(
                bookProductId = it.bookProductId,
                title = it.title,
                publisher = it.publisher,
                authorNames = it.authorNames,
                isbn = it.isbn,
                availableCount = it.availableCount,
                totalCount = it.totalCount,
            )
        }
}

data class BookItemSearchResponse(
    @JsonProperty("book_items")
    val items: List<BookItemSearchItem>,
)

data class BookItemSearchItem(
    @JsonProperty("book_product_id")
    val bookProductId: String,
    val title: String,
    val publisher: String,
    @JsonProperty("author_names")
    val authorNames: List<String>,
    val isbn: String,
    @JsonProperty("available_count")
    val availableCount: Int,
    @JsonProperty("total_count")
    val totalCount: Int,
)
