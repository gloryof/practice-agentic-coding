package jp.glory.practice.agentic.libraryuser.command.domain.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.shared.domain.DomainError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserRegistrationServiceTest {
    @Test
    fun `returns ok when email is unique`() {
        val repository = mockk<LibraryUserCommandRepository>()
        every { repository.existsByEmail(any()) } returns EmailExistence(false)
        val service = LibraryUserRegistrationService(repository)
        val email = Email.create("user@example.com").fold(
            success = { it },
            failure = { error("expected success") },
        )

        val result = service.verify(email)
        assertEquals(Ok(Unit), result)
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `returns duplicate email error`() {
        val repository = mockk<LibraryUserCommandRepository>()
        every { repository.existsByEmail(any()) } returns EmailExistence(true)
        val service = LibraryUserRegistrationService(repository)
        val email = Email.create("user@example.com").fold(
            success = { it },
            failure = { error("expected success") },
        )

        val result = service.verify(email)
        assertEquals(Err(DomainError.DuplicateEmail), result)
        verify(exactly = 0) { repository.save(any()) }
    }
}
