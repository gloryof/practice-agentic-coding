package jp.glory.practice.agentic.auth.command.infra.adapter.credential

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.getOrThrow
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.shared.domain.DomainError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthCredentialProvisionerImplTest {
    @Nested
    inner class Validate {
        @Test
        fun `given valid password when validate then returns redacted validated password`() {
            val sut = createSut()

            val validated = sut.validate("Str0ng!Passw0rd").getOrThrow { error("expected success") }

            assertEquals("ValidatedAuthPassword(REDACTED)", validated.toString())
        }

        @Test
        fun `given weak password when validate then returns policy error`() {
            val sut = createSut()

            val result = sut.validate("short")

            assertEquals(
                Err(DomainError.Validation(field = "password", reason = "must_meet_password_policy")),
                result,
            )
        }
    }

    @Nested
    inner class Provision {
        @Test
        fun `given validated password when provision then hashes and saves credential`() {
            val repository = mockk<AuthCredentialRepository>()
            val credentialSlot = slot<AuthCredential>()
            every { repository.save(capture(credentialSlot)) } just Runs
            val sut = createSut(repository)
            val password = sut.validate("Str0ng!Passw0rd").getOrThrow { error("expected success") }

            sut.provision("user-id", password)

            verify(exactly = 1) { repository.save(any()) }
            assertEquals("user-id", credentialSlot.captured.libraryUserId.value)
            assertEquals("hashed-Str0ng!Passw0rd", credentialSlot.captured.passwordHash.value)
        }
    }

    private fun createSut(
        repository: AuthCredentialRepository = mockk(relaxed = true),
    ): AuthCredentialProvisionerImpl =
        AuthCredentialProvisionerImpl(
            authCredentialRepository = repository,
            passwordHasher =
                PasswordHasher {
                    PasswordHash.create("hashed-$it").getOrThrow { error("expected valid hash") }
                },
        )
}
