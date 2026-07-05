package jp.glory.practice.agentic.catalog.query.usecase

import org.springframework.stereotype.Service

@Service
class BookItemSearchUseCase(
    private val query: BookItemSearchQuery,
) {
    fun search(input: BookItemSearchInput): List<BookItemSearchResults> = mapResults(query.search(input))

    private fun mapResults(results: List<BookItemSearchResult>) = results.map { it.toSearchResults() }

    private fun BookItemSearchResult.toSearchResults(): BookItemSearchResults =
        BookItemSearchResults(
            bookProductId = bookProductId,
            title = title,
            publisher = publisher,
            authorNames = authorNames,
            isbn = isbn,
            availableCount = availableCount,
            totalCount = totalCount,
        )
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
    val bookProductId: String,
    val title: String,
    val publisher: String,
    val authorNames: List<String>,
    val isbn: String,
    val availableCount: Int,
    val totalCount: Int,
)

data class BookItemSearchResult(
    val bookProductId: String,
    val title: String,
    val publisher: String,
    val authorNames: List<String>,
    val isbn: String,
    val availableCount: Int,
    val totalCount: Int,
)

interface BookItemSearchQuery {
    fun search(input: BookItemSearchInput): List<BookItemSearchResult>
}
