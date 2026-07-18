package jp.glory.practice.agentic.shared.auth

import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.shared.domain.DomainError

interface AuthCredentialProvisioner {
    fun validate(rawPassword: String): Result<ValidatedAuthPassword, DomainError>

    fun provision(
        libraryUserId: String,
        password: ValidatedAuthPassword,
    )
}

class ValidatedAuthPassword internal constructor(
    internal val value: String,
) {
    override fun toString(): String = "ValidatedAuthPassword(REDACTED)"
}
