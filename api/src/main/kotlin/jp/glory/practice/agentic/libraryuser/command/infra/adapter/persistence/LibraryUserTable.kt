package jp.glory.practice.agentic.libraryuser.command.infra.adapter.persistence

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import java.time.Instant

@KomapperEntity
data class LibraryUserTable(
    @KomapperId
    val id: String,
    val email: String,
    val registeredAt: Instant,
)
