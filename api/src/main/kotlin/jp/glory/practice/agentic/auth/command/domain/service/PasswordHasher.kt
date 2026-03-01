package jp.glory.practice.agentic.auth.command.domain.service

import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash

fun interface PasswordHasher {
    fun hash(rawPassword: String): PasswordHash
}

fun interface PasswordVerifier {
    fun matches(rawPassword: String, hash: PasswordHash): Boolean
}

fun interface AccessTokenGenerator {
    fun generate(): String
}
