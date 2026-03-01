package jp.glory.practice.agentic.shared.spring

import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.auth.command.domain.service.PasswordVerifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.UUID

@Configuration
class SecurityInfraConfig {
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun passwordHasher(encoder: BCryptPasswordEncoder): PasswordHasher = PasswordHasher { raw ->
        // Technical guard: encoder failure is unexpected and should be handled by global 500.
        encoder.encode(raw) ?: throw IllegalStateException("password hash generation failed")
    }

    @Bean
    fun passwordVerifier(encoder: BCryptPasswordEncoder): PasswordVerifier = PasswordVerifier { raw, hash ->
        encoder.matches(raw, hash)
    }

    @Bean
    fun accessTokenGenerator(): AccessTokenGenerator = AccessTokenGenerator {
        UUID.randomUUID().toString()
    }
}
