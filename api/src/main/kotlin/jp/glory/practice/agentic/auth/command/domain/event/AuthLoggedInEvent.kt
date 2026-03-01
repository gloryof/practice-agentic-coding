package jp.glory.practice.agentic.auth.command.domain.event

import jp.glory.practice.agentic.auth.command.domain.model.AuthAccount
import java.time.Instant

data class AuthLoggedInEvent(
    val account: AuthAccount,
    val occurredAt: Instant,
)
