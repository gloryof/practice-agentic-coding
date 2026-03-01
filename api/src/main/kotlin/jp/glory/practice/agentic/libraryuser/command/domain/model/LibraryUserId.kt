package jp.glory.practice.agentic.libraryuser.command.domain.model

import java.util.UUID

@JvmInline
value class LibraryUserId(val value: String) {
    companion object {
        fun issue(): LibraryUserId {
            return LibraryUserId(
                UUID.randomUUID().toString().replace("-", "").take(26)
            )
        }
    }
}
