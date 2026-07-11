package jp.glory.practice.agentic.shared.spring

import jp.glory.practice.agentic.reservation.command.domain.constraint.ReservationEligibility
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReservationCommandConfig {
    @Bean
    fun reservationEligibility(): ReservationEligibility = ReservationEligibility()
}
