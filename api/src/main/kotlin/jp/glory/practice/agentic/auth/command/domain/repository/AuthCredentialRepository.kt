package jp.glory.practice.agentic.auth.command.domain.repository

import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential

interface AuthCredentialRepository {
    fun save(libraryUserId: String, passwordHash: String)
    fun findByLibraryUserId(libraryUserId: String): AuthCredential?
}
