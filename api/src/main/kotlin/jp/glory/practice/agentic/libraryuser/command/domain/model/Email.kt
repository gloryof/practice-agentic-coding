package jp.glory.practice.agentic.libraryuser.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.shared.domain.DomainError

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val halfWidthRegex = Regex("^[\\u0020-\\u007E]+$")

        fun create(raw: String): Result<Email, DomainError> {
            if (raw.isBlank()) {
                return Err(DomainError.Validation(field = "email", reason = "required"))
            }
            if (!halfWidthRegex.matches(raw)) {
                return Err(DomainError.Validation(field = "email", reason = "must_be_half_width"))
            }
            return Ok(Email(raw))
        }
    }
}
