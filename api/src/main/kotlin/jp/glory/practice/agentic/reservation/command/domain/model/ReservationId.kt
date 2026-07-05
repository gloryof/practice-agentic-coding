package jp.glory.practice.agentic.reservation.command.domain.model

import java.util.UUID

@JvmInline
value class ReservationId(
    val value: String,
) {
    companion object {
        fun issue(): ReservationId = ReservationId(UUID.randomUUID().toString())
    }
}
