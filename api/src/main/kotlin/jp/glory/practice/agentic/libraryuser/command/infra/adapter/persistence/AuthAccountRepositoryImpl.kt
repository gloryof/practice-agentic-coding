package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.auth.command.domain.repository.AuthAccountRepository
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.LibraryUserDao
import org.springframework.stereotype.Repository

@Repository
class AuthAccountRepositoryImpl(
    private val libraryUserDao: LibraryUserDao,
) : AuthAccountRepository {
    override fun findByEmail(email: Email): AuthAccount? {
        val user = libraryUserDao.findByEmail(email.value) ?: return null

        val storedEmail =
            Email.create(user.email).fold(
                success = { it },
                failure = { throw IllegalStateException("Invalid email stored in library_users: ${user.email}") },
            )

        return AuthAccount(
            libraryUserId = LibraryUserId(user.id),
            email = storedEmail,
        )
    }
}
