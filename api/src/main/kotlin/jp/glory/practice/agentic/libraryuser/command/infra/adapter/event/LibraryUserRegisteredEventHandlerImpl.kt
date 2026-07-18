package jp.glory.practice.agentic.libraryuser.command.infra.adapter.event

import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEventHandler
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import org.springframework.stereotype.Component

@Component
class LibraryUserRegisteredEventHandlerImpl(
    private val libraryUserRepository: LibraryUserCommandRepository,
) : LibraryUserRegisteredEventHandler {
    override fun handle(event: LibraryUserRegisteredEvent) {
        libraryUserRepository.save(event.toLibraryUser(), event.occurredAt)
    }
}
