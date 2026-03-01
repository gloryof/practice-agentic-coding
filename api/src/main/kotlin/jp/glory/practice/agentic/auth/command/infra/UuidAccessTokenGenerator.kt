package jp.glory.practice.agentic.auth.command.infra

import jp.glory.practice.agentic.auth.command.domain.service.AccessTokenGenerator
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidAccessTokenGenerator : AccessTokenGenerator {
    override fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
