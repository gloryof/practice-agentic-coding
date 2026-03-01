package jp.glory.practice.agentic.libraryuser.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.shared.domain.DomainError
import kotlin.test.Test
import kotlin.test.assertEquals

class RawPasswordTest {
    @Test
    fun `accepts valid password`() {
        val result = RawPassword.create("Str0ng!Passw0rd")
        result.fold(
            success = { assertEquals("Str0ng!Passw0rd", it.value) },
            failure = { error("expected success") },
        )
    }

    @Test
    fun `rejects short password`() {
        val result = RawPassword.create("Abc1!short")
        assertEquals(Err(DomainError.Validation(field = "password", reason = "must_meet_password_policy")), result)
    }
}
