package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import org.springframework.stereotype.Repository

@Repository
class LibraryUserCommandRepositoryImpl(
    private val libraryUserDao: LibraryUserDao,
) : LibraryUserCommandRepository {
    override fun save(event: LibraryUserRegisteredEvent) {
        libraryUserDao.insert(
            LibraryUserTable(
                id = event.libraryUserId.value,
                email = event.email.value,
                registeredAt = event.occurredAt,
            ),
        )
    }

    override fun existsByEmail(email: Email): EmailExistence = EmailExistence(libraryUserDao.existsByEmail(email.value))
}
