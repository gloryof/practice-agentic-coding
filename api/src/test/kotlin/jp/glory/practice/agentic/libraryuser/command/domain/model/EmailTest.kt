package jp.glory.practice.agentic.libraryuser.command.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.shared.domain.DomainError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailTest {
    @Nested
    inner class Create {
        @Test
        fun `given half width email when create then returns value`() {
            val result = Email.create("user@example.com")
            result.fold(
                success = { assertEquals("user@example.com", it.value) },
                failure = { error("expected success") },
            )
        }

        @Test
        fun `given full width email when create then returns validation error`() {
            val result = Email.create("ｕser@example.com")
            assertEquals(Err(DomainError.Validation(field = "email", reason = "must_be_half_width")), result)
        }
    }
}
