package jp.glory.practice.agentic.libraryuser.command.domain.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.fold
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.shared.domain.DomainError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegistrationServiceTest {
    private data class TestContext(
        val sut: LibraryUserRegistrationService,
        val repository: LibraryUserCommandRepository,
    )

    @Nested
    inner class Verify {
        @Test
        fun `given unique email when verify then returns ok`() {
            val context = createSut()
            every { context.repository.existsByEmail(any()) } returns EmailExistence(false)
            val email =
                Email.create("user@example.com").fold(
                    success = { it },
                    failure = { error("expected success") },
                )

            val result = context.sut.verify(email)
            assertEquals(Ok(Unit), result)
            verify(exactly = 0) { context.repository.save(any(), any()) }
        }

        @Test
        fun `given duplicated email when verify then returns duplicate email error`() {
            val context = createSut()
            every { context.repository.existsByEmail(any()) } returns EmailExistence(true)
            val email =
                Email.create("user@example.com").fold(
                    success = { it },
                    failure = { error("expected success") },
                )

            val result = context.sut.verify(email)
            assertEquals(Err(DomainError.DuplicateEmail), result)
            verify(exactly = 0) { context.repository.save(any(), any()) }
        }
    }

    private fun createSut(repository: LibraryUserCommandRepository = mockk()): TestContext {
        val sut = LibraryUserRegistrationService(repository)
        return TestContext(sut = sut, repository = repository)
    }
}
