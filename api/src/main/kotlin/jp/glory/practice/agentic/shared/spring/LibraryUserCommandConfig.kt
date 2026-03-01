package jp.glory.practice.agentic.shared.spring

import jp.glory.practice.agentic.libraryuser.command.domain.repository.LibraryUserCommandRepository
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class LibraryUserCommandConfig {
    @Bean
    fun systemClock(): Clock = Clock.systemUTC()

    @Bean
    fun libraryUserRegistrationService(
        repository: LibraryUserCommandRepository,
    ): LibraryUserRegistrationService {
        return LibraryUserRegistrationService(repository)
    }
}
