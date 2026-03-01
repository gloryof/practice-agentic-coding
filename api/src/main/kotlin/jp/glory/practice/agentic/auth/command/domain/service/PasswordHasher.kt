package jp.glory.practice.agentic.auth.command.domain.service

fun interface PasswordHasher {
    fun hash(rawPassword: String): String
}

fun interface PasswordVerifier {
    fun matches(rawPassword: String, hash: String): Boolean
}

fun interface AccessTokenGenerator {
    fun generate(): String
}
