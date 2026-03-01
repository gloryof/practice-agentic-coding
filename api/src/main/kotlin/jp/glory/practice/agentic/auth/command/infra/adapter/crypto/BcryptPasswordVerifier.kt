package jp.glory.practice.agentic.auth.command.infra.adapter.crypto

import jp.glory.practice.agentic.auth.command.domain.model.PasswordHash
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BcryptPasswordVerifier(
    private val encoder: BCryptPasswordEncoder,
) : PasswordVerifier {
    override fun matches(rawPassword: String, hash: PasswordHash): Boolean {
        return encoder.matches(rawPassword, hash.value)
    }
}
