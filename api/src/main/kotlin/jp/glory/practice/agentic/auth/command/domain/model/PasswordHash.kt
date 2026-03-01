package jp.glory.practice.agentic.auth.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.shared.domain.DomainError

@JvmInline
value class PasswordHash private constructor(val value: String) {
    companion object {
        fun create(raw: String): Result<PasswordHash, DomainError> {
            if (raw.isBlank()) {
                return Err(DomainError.Validation(field = "password_hash", reason = "required"))
            }
            return Ok(PasswordHash(raw))
        }
    }
}
