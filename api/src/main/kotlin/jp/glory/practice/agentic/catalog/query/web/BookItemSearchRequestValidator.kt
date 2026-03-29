package jp.glory.practice.agentic.catalog.query.web

import jp.glory.practice.agentic.catalog.query.usecase.BookItemSearchInput
import jp.glory.practice.agentic.shared.web.ApiErrorDetail
import jp.glory.practice.agentic.shared.web.ValidationApiException
import org.springframework.stereotype.Component

@Component
class BookItemSearchRequestValidator {
    fun validateAndConvert(request: BookItemSearchRequest): BookItemSearchInput {
        val parsed = parseRequest(request)
        if (parsed.details.isNotEmpty()) {
            throw ValidationApiException(parsed.details)
        }
        return parsed.input
    }

    private fun parseRequest(request: BookItemSearchRequest): ParsedRequest {
        val details = mutableListOf<ApiErrorDetail>()
        val values = collectValues(request, details)
        val input =
            BookItemSearchInput(
                title = values.title,
                titleExact = values.titleExact,
                titleKana = values.titleKana,
                titleKanaExact = values.titleKanaExact,
                publisher = values.publisher,
                publisherExact = values.publisherExact,
                publisherKana = values.publisherKana,
                publisherKanaExact = values.publisherKanaExact,
                authorName = values.authorName,
                authorExact = values.authorExact,
                authorNameKana = values.authorNameKana,
                authorKanaExact = values.authorKanaExact,
                isbn = values.isbn,
            )
        return ParsedRequest(input = input, details = details)
    }

    private fun collectValues(
        request: BookItemSearchRequest,
        details: MutableList<ApiErrorDetail>,
    ): ParsedValues {
        val textValues = collectTextValues(request, details)
        val exactFlags = collectExactFlags(request, textValues, details)
        validateCriteria(textValues, details)
        return ParsedValues(
            title = textValues.title,
            titleExact = exactFlags.titleExact,
            titleKana = textValues.titleKana,
            titleKanaExact = exactFlags.titleKanaExact,
            publisher = textValues.publisher,
            publisherExact = exactFlags.publisherExact,
            publisherKana = textValues.publisherKana,
            publisherKanaExact = exactFlags.publisherKanaExact,
            authorName = textValues.authorName,
            authorExact = exactFlags.authorExact,
            authorNameKana = textValues.authorNameKana,
            authorKanaExact = exactFlags.authorKanaExact,
            isbn = textValues.isbn,
        )
    }

    private fun collectTextValues(
        request: BookItemSearchRequest,
        details: MutableList<ApiErrorDetail>,
    ): TextValues =
        TextValues(
            title = validateTextField("title", request.title, details),
            titleKana = validateTextField("title_kana", request.titleKana, details),
            publisher = validateTextField("publisher", request.publisher, details),
            publisherKana = validateTextField("publisher_kana", request.publisherKana, details),
            authorName = validateTextField("author_name", request.authorName, details),
            authorNameKana = validateTextField("author_name_kana", request.authorNameKana, details),
            isbn = validateTextField("isbn", request.isbn, details),
        )

    private fun collectExactFlags(
        request: BookItemSearchRequest,
        values: TextValues,
        details: MutableList<ApiErrorDetail>,
    ): ExactFlags {
        val titleExact =
            parseExactFlag("title_exact", request.titleExact, values.title != null, details)
        val titleKanaExact =
            parseExactFlag("title_kana_exact", request.titleKanaExact, values.titleKana != null, details)
        val publisherExact =
            parseExactFlag("publisher_exact", request.publisherExact, values.publisher != null, details)
        val publisherKanaExact =
            parseExactFlag("publisher_kana_exact", request.publisherKanaExact, values.publisherKana != null, details)
        val authorExact = parseExactFlag("author_exact", request.authorExact, values.authorName != null, details)
        val authorKanaExact =
            parseExactFlag("author_kana_exact", request.authorKanaExact, values.authorNameKana != null, details)
        return ExactFlags(
            titleExact = titleExact,
            titleKanaExact = titleKanaExact,
            publisherExact = publisherExact,
            publisherKanaExact = publisherKanaExact,
            authorExact = authorExact,
            authorKanaExact = authorKanaExact,
        )
    }

    private fun validateCriteria(
        values: TextValues,
        details: MutableList<ApiErrorDetail>,
    ) {
        if (
            values.title == null &&
            values.titleKana == null &&
            values.publisher == null &&
            values.publisherKana == null &&
            values.authorName == null &&
            values.authorNameKana == null &&
            values.isbn == null
        ) {
            details.add(ApiErrorDetail(field = "criteria", reason = "required"))
        }
    }

    private fun validateTextField(
        fieldName: String,
        raw: String?,
        details: MutableList<ApiErrorDetail>,
    ): String? {
        if (raw == null) {
            return null
        }
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            details.add(ApiErrorDetail(field = fieldName, reason = "required"))
            return null
        }
        return trimmed
    }

    private fun parseExactFlag(
        fieldName: String,
        raw: String?,
        hasValue: Boolean,
        details: MutableList<ApiErrorDetail>,
    ): Boolean {
        if (raw == null) {
            return false
        }
        if (!hasValue) {
            details.add(ApiErrorDetail(field = fieldName, reason = "requires_value"))
            return false
        }
        if (raw != "true") {
            details.add(ApiErrorDetail(field = fieldName, reason = "must_be_true"))
            return false
        }
        return true
    }
}

private data class ParsedRequest(
    val input: BookItemSearchInput,
    val details: List<ApiErrorDetail>,
)

private data class ParsedValues(
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

private data class TextValues(
    val title: String?,
    val titleKana: String?,
    val publisher: String?,
    val publisherKana: String?,
    val authorName: String?,
    val authorNameKana: String?,
    val isbn: String?,
)

private data class ExactFlags(
    val titleExact: Boolean,
    val titleKanaExact: Boolean,
    val publisherExact: Boolean,
    val publisherKanaExact: Boolean,
    val authorExact: Boolean,
    val authorKanaExact: Boolean,
)

data class BookItemSearchRequest(
    val title: String? = null,
    val titleKana: String? = null,
    val publisher: String? = null,
    val publisherKana: String? = null,
    val authorName: String? = null,
    val authorNameKana: String? = null,
    val isbn: String? = null,
    val titleExact: String? = null,
    val titleKanaExact: String? = null,
    val publisherExact: String? = null,
    val publisherKanaExact: String? = null,
    val authorExact: String? = null,
    val authorKanaExact: String? = null,
)
