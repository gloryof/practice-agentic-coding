package jp.glory.practice.agentic.libraryuser.command.domain.event

import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import java.time.Instant

data class LibraryUserRegisteredEvent(
    val libraryUserId: LibraryUserId,
    val email: Email,
    val occurredAt: Instant,
)
