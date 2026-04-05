package jp.glory.practice.agentic.libraryuser.command.domain.model

import java.util.UUID

@JvmInline
value class LibraryUserId(
    val value: String,
) {
    companion object {
        fun issue(): LibraryUserId =
            LibraryUserId(
                UUID
                    .randomUUID()
                    .toString(),
            )
    }
}
