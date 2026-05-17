package jp.glory.practice.agentic.auth.command.infra.adapter.persistence

import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.shared.infra.adapter.persistence.dao.AuthCredentialDao
import jp.glory.practice.agentic.shared.infra.adapter.persistence.table.AuthCredentialTable
import org.springframework.stereotype.Repository

@Repository
class AuthCredentialRepositoryImpl(
    private val authCredentialDao: AuthCredentialDao,
) : AuthCredentialRepository {
    override fun save(credential: AuthCredential) {
        authCredentialDao.insert(
            AuthCredentialTable(
                id = credential.libraryUserId.value,
                libraryUserId = credential.libraryUserId.value,
                passwordHash = credential.passwordHash.value,
            ),
        )
    }

    override fun findByLibraryUserId(libraryUserId: LibraryUserId): AuthCredential? {
        val credential = authCredentialDao.findByLibraryUserId(libraryUserId.value) ?: return null

        return AuthCredential(
            libraryUserId = LibraryUserId(credential.libraryUserId),
            passwordHash =
                PasswordHash.create(credential.passwordHash).fold(
                    success = { it },
                    failure = { throw IllegalStateException("Invalid password hash stored in auth_credentials") },
                ),
        )
    }
}
