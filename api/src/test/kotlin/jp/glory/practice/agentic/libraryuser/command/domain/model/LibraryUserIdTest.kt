package jp.glory.practice.agentic.libraryuser.command.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserIdTest {
    @Test
    fun `given id issuance when issue then returns fixed length id`() {
        val id = LibraryUserId.issue()

        assertEquals(36, id.value.length)
    }
}
