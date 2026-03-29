package jp.glory.practice.agentic.catalog.query.usecase

import org.springframework.stereotype.Service

@Service
class BookItemSearchUseCase(
    private val query: BookItemSearchQuery,
) {
    fun search(input: BookItemSearchInput): List<BookItemSearchResults> =
        query
            .search(input)
            .map {
                BookItemSearchResults(
                    title = it.title,
                    publisher = it.publisher,
                    authorNames = it.authorNames,
                    isbn = it.isbn,
                )
            }
}

data class BookItemSearchInput(
    val title: String?,
    val titleExact: Boolean,
    val titleKana: String?,
    val titleKanaExact: Boolean,
    val publisher: String?,
    val publisherExact: Boolean,
    val publisherKana: String?,
    val publisherKanaExact: Boolean,
    val authorName: String?,
    val authorExact: Boolean,
    val authorNameKana: String?,
    val authorKanaExact: Boolean,
    val isbn: String?,
)

data class BookItemSearchResults(
    val title: String,
    val publisher: String,
    val authorNames: List<String>,
    val isbn: String,
)

data class BookItemSearchResult(
    val bookItemId: String,
    val title: String,
    val publisher: String,
    val authorNames: List<String>,
    val isbn: String,
)

interface BookItemSearchQuery {
    fun search(input: BookItemSearchInput): List<BookItemSearchResult>
}
