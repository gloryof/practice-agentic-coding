package jp.glory.practice.agentic.auth.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.shared.domain.DomainError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class RawPasswordTest {
    @Nested
    inner class Create {
        @Test
        fun `given valid password when create then returns value`() {
            val result = RawPassword.create("Str0ng!Passw0rd")
            result.fold(
                success = { assertEquals("Str0ng!Passw0rd", it.value) },
                failure = { error("expected success") },
            )
        }

        @Test
        fun `given short password when create then returns validation error`() {
            val result = RawPassword.create("Abc1!short")
            assertEquals(Err(DomainError.Validation(field = "password", reason = "must_meet_password_policy")), result)
        }
    }
}
