package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.EmailExistence
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUser
import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.LibraryUserTable
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class LibraryUserCommandRepositoryImpl(
    private val libraryUserDao: LibraryUserDao,
) : LibraryUserCommandRepository {
    override fun save(
        libraryUser: LibraryUser,
        registeredAt: Instant,
    ) {
        libraryUserDao.insert(
            LibraryUserTable(
                id = libraryUser.id.value,
                email = libraryUser.email.value,
                registeredAt = registeredAt,
            ),
        )
    }

    override fun existsByEmail(email: Email): EmailExistence = EmailExistence(libraryUserDao.existsByEmail(email.value))
}
