package jp.glory.practice.agentic.auth.command.domain.repository

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount

interface AuthAccountRepository {
    fun findByEmail(email: String): AuthAccount?
}
