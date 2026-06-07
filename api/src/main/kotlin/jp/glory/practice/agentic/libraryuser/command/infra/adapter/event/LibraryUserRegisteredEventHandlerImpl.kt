package jp.glory.practice.agentic.libraryuser.command.infra.adapter.event

import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEventHandler
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import org.springframework.stereotype.Component

@Component
class LibraryUserRegisteredEventHandlerImpl(
    private val libraryUserRepository: LibraryUserCommandRepository,
    private val authCredentialRepository: AuthCredentialRepository,
    private val passwordHasher: PasswordHasher,
) : LibraryUserRegisteredEventHandler {
    override fun handle(event: LibraryUserRegisteredEvent) {
        libraryUserRepository.save(event.toLibraryUser(), event.occurredAt)
        authCredentialRepository.save(
            AuthCredential(
                libraryUserId = event.libraryUserId,
                passwordHash = passwordHasher.hash(event.rawPassword.value),
            ),
        )
    }
}
