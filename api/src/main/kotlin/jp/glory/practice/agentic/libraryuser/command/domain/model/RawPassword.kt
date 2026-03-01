package jp.glory.practice.agentic.libraryuser.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.shared.domain.DomainError

@JvmInline
value class RawPassword private constructor(val value: String) {
    companion object {
        private val uppercaseRegex = Regex(".*[A-Z].*")
        private val lowercaseRegex = Regex(".*[a-z].*")
        private val numberRegex = Regex(".*[0-9].*")
        private val symbolRegex = Regex(".*[^A-Za-z0-9].*")

        fun create(raw: String): Result<RawPassword, DomainError> {
            if (raw.isBlank()) {
                return Err(DomainError.Validation(field = "password", reason = "required"))
            }
            if (raw.length < 12 ||
                !uppercaseRegex.matches(raw) ||
                !lowercaseRegex.matches(raw) ||
                !numberRegex.matches(raw) ||
                !symbolRegex.matches(raw)
            ) {
                return Err(DomainError.Validation(field = "password", reason = "must_meet_password_policy"))
            }
            return Ok(RawPassword(raw))
        }
    }
}
