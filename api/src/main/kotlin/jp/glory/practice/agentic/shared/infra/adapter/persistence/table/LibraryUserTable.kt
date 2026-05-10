package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable
import java.time.Instant

@KomapperEntity
@KomapperTable(name = "library_users")
data class LibraryUserTable(
    @KomapperId
    val id: String,
    val email: String,
    val registeredAt: Instant,
)
