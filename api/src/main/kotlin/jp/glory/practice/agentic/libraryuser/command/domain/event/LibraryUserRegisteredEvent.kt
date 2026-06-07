package jp.glory.practice.agentic.libraryuser.command.domain.event

import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.model.RawPassword
import java.time.Instant

data class LibraryUserRegisteredEvent(
    val libraryUserId: LibraryUserId,
    val email: Email,
    val rawPassword: RawPassword,
    val occurredAt: Instant,
) {
    fun toLibraryUser(): LibraryUser =
        LibraryUser(
            id = libraryUserId,
            email = email,
        )
}

fun interface LibraryUserRegisteredEventHandler {
    fun handle(event: LibraryUserRegisteredEvent)
}
