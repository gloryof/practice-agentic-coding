package jp.glory.practice.agentic.auth.command.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginUseCaseTest {
    private data class TestContext(
        val sut: LoginUseCase,
        val authAccountRepository: AuthAccountRepository,
        val authCredentialRepository: AuthCredentialRepository,
    )

    private fun hashed(value: String): PasswordHash =
        PasswordHash.create(value).fold(
            success = { it },
            failure = { error("expected valid password hash") },
        )

    @Test
    fun `returns token on valid credentials`() {
        val context =
            createSut(
                passwordVerifier = PasswordVerifier { raw, hash -> raw == "Str0ng!Passw0rd" && hash.value == "hashed" },
            )
        stubUserLookup(context)

        val result = context.sut.login(LoginInput("user@example.com", "Str0ng!Passw0rd"))
        result.fold(
            success = {
                assertEquals("token-123", it.accessToken)
                assertEquals("Bearer", it.tokenType)
                assertEquals(86400, it.expiresInSeconds)
            },
            failure = { error("expected success") },
        )
        verify(exactly = 0) { context.authCredentialRepository.save(any()) }
    }

    @Test
    fun `returns err on invalid credentials`() {
        val context = createSut(passwordVerifier = PasswordVerifier { _, _ -> false })
        stubUserLookup(context)

        val result = context.sut.login(LoginInput("user@example.com", "Str0ng!Wrong12"))
        assertEquals(Err(UsecaseError.AuthenticationFailed), result)
        verify(exactly = 0) { context.authCredentialRepository.save(any()) }
    }

    @Test
    fun `returns validation error on invalid email`() {
        val context = createSut(passwordVerifier = PasswordVerifier { _, _ -> true })

        val result = context.sut.login(LoginInput("", "Str0ng!Passw0rd"))
        assertEquals(Err(UsecaseError.Validation(field = "email", reason = "required")), result)
        verify(exactly = 0) { context.authCredentialRepository.save(any()) }
    }

    private fun createSut(
        authAccountRepository: AuthAccountRepository = mockk(),
        authCredentialRepository: AuthCredentialRepository = mockk(),
        passwordVerifier: PasswordVerifier,
        accessTokenGenerator: AccessTokenGenerator = AccessTokenGenerator { "token-123" },
        expirationSeconds: Long = 86400,
    ): TestContext {
        val sut =
            LoginUseCase(
                authAccountRepository = authAccountRepository,
                authCredentialRepository = authCredentialRepository,
                passwordVerifier = passwordVerifier,
                accessTokenGenerator = accessTokenGenerator,
                expirationSeconds = expirationSeconds,
            )
        return TestContext(
            sut = sut,
            authAccountRepository = authAccountRepository,
            authCredentialRepository = authCredentialRepository,
        )
    }

    private fun stubUserLookup(context: TestContext) {
        val email =
            Email.create("user@example.com").fold(
                success = { it },
                failure = { error("expected valid email") },
            )
        every { context.authAccountRepository.findByEmail(any()) } returns AuthAccount(LibraryUserId("user-id"), email)
        every { context.authCredentialRepository.findByLibraryUserId(any()) } returns
            AuthCredential(
                LibraryUserId("user-id"),
                hashed("hashed"),
            )
    }
}
