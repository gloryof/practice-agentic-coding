package jp.glory.practice.agentic.libraryuser.command.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryUserIdTest {
    @Test
    fun `issues id with fixed length`() {
        val id = LibraryUserId.issue()

        assertEquals(26, id.value.length)
    }
}
