package jp.glory.practice.agentic.auth.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.zip
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.RawPassword
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val authAccountRepository: AuthAccountRepository,
    private val authCredentialRepository: AuthCredentialRepository,
    private val passwordVerifier: PasswordVerifier,
    private val accessTokenGenerator: AccessTokenGenerator,
    @Value("\${auth.token.expiration-seconds}") private val expirationSeconds: Long,
) {
    private data class ValidatedInput(
        val email: Email,
        val rawPassword: RawPassword,
    )

    fun login(input: LoginInput): Result<LoginResult, UsecaseError> =
        zip(
            { Email.create(input.email) },
            { RawPassword.create(input.password) },
        ) { email, rawPassword ->
            ValidatedInput(email = email, rawPassword = rawPassword)
        }
            .mapError(UsecaseError::fromDomain)
            .andThen(::authenticate)

    private fun authenticate(input: ValidatedInput): Result<LoginResult, UsecaseError> {
        val user = authAccountRepository.findByEmail(input.email) ?: return Err(UsecaseError.AuthenticationFailed)
        val credential = authCredentialRepository.findByLibraryUserId(user.libraryUserId)
            ?: return Err(UsecaseError.AuthenticationFailed)
        if (!passwordVerifier.matches(input.rawPassword.value, credential.passwordHash)) {
            return Err(UsecaseError.AuthenticationFailed)
        }
        return Ok(
            LoginResult(
                accessToken = accessTokenGenerator.generate(),
                tokenType = "Bearer",
                expiresInSeconds = expirationSeconds,
            )
        )
    }
}

data class LoginInput(
    val email: String,
    val password: String,
)

data class LoginResult(
    val accessToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
)
