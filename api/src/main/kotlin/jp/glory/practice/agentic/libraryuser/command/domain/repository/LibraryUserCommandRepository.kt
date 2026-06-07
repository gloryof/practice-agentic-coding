package jp.glory.practice.agentic.libraryuser.command.domain.repository

import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import java.time.Instant

interface LibraryUserCommandRepository {
    fun save(
        libraryUser: LibraryUser,
        registeredAt: Instant,
    )

    fun existsByEmail(email: Email): EmailExistence
}
