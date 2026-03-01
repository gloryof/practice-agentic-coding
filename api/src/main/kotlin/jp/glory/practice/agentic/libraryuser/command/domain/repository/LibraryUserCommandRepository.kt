package jp.glory.practice.agentic.libraryuser.command.domain.repository

import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence

interface LibraryUserCommandRepository {
    fun save(event: LibraryUserRegisteredEvent)
    fun existsByEmail(email: Email): EmailExistence
}
