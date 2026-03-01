package jp.glory.practice.agentic.libraryuser.command.domain.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.shared.domain.DomainError

class LibraryUserRegistrationService(
    private val repository: LibraryUserCommandRepository,
) {
    fun verify(email: Email): Result<Unit, DomainError> {
        if (repository.existsByEmail(email).value) {
            return Err(DomainError.DuplicateEmail)
        }
        return Ok(Unit)
    }
}
