package jp.glory.practice.agentic.auth.command.domain.repository

import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId

interface AuthCredentialRepository {
    fun save(credential: AuthCredential)
    fun findByLibraryUserId(libraryUserId: LibraryUserId): AuthCredential?
}
