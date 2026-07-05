package jp.glory.practice.agentic.shared.infra.adapter.persistence.table

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable
import java.time.Instant

@KomapperEntity
@KomapperTable(name = "reservations")
data class ReservationTable(
    @KomapperId
    val id: String,
    val libraryUserId: String,
    val bookProductId: String,
    val bookItemId: String,
    val reservedAt: Instant,
)
