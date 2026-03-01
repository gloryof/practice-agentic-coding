package jp.glory.practice.agentic.auth.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginUseCaseTest {
    private fun buildUseCase(passwordVerifier: PasswordVerifier): LoginUseCase =
        LoginUseCase(
            authAccountRepository = object : AuthAccountRepository {
                override fun findByEmail(email: String) = AuthAccount("user-id", "user@example.com")
            },
            authCredentialRepository = object : AuthCredentialRepository {
                override fun save(libraryUserId: String, passwordHash: String) = Unit
                override fun findByLibraryUserId(libraryUserId: String) = AuthCredential("user-id", "hashed")
            },
            passwordVerifier = passwordVerifier,
            accessTokenGenerator = AccessTokenGenerator { "token-123" },
            expirationSeconds = 86400,
        )

    @Test
    fun `returns token on valid credentials`() {
        val useCase = buildUseCase(PasswordVerifier { raw, hash -> raw == "Str0ng!Passw0rd" && hash == "hashed" })

        val result = useCase.login(LoginInput("user@example.com", "Str0ng!Passw0rd"))
        result.fold(
            success = {
                assertEquals("token-123", it.accessToken)
                assertEquals("Bearer", it.tokenType)
                assertEquals(86400, it.expiresInSeconds)
            },
            failure = { error("expected success") },
        )
    }

    @Test
    fun `returns err on invalid credentials`() {
        val useCase = buildUseCase(PasswordVerifier { _, _ -> false })

        val result = useCase.login(LoginInput("user@example.com", "Str0ng!Wrong12"))
        assertEquals(Err(UsecaseError.AuthenticationFailed), result)
    }

    @Test
    fun `returns validation error on invalid email`() {
        val useCase = buildUseCase(PasswordVerifier { _, _ -> true })

        val result = useCase.login(LoginInput("", "Str0ng!Passw0rd"))
        assertEquals(Err(UsecaseError.Validation(field = "email", reason = "required")), result)
    }
}
