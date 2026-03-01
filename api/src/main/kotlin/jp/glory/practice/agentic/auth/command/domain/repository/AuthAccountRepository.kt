package jp.glory.practice.agentic.auth.command.domain.repository

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email

interface AuthAccountRepository {
    fun findByEmail(email: Email): AuthAccount?
}
