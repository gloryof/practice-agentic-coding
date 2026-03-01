package jp.glory.practice.agentic.libraryuser.command.domain.repository

import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email

interface LibraryUserCommandRepository {
    fun save(event: LibraryUserRegisteredEvent)
    fun existsByEmail(email: Email): Boolean
}
