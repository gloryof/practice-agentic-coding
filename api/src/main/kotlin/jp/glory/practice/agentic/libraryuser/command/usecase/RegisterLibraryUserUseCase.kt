package jp.glory.practice.agentic.libraryuser.command.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.zip
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEvent
import jp.glory.practice.agentic.libraryuser.command.domain.event.LibraryUserRegisteredEventHandler
import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.libraryuser.command.domain.service.LibraryUserRegistrationService
import jp.glory.practice.agentic.shared.auth.AuthCredentialProvisioner
import jp.glory.practice.agentic.shared.auth.ValidatedAuthPassword
import jp.glory.practice.agentic.shared.usecase.UsecaseError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class RegisterLibraryUserUseCase(
    private val registrationService: LibraryUserRegistrationService,
    private val libraryUserRegisteredEventHandler: LibraryUserRegisteredEventHandler,
    private val authCredentialProvisioner: AuthCredentialProvisioner,
    private val clock: Clock,
) {
    private data class ValidatedInput(
        val email: Email,
        val password: ValidatedAuthPassword,
    )

    private data class RegistrationContext(
        val libraryUserId: LibraryUserId,
        val registeredAt: Instant,
        val event: LibraryUserRegisteredEvent,
        val password: ValidatedAuthPassword,
    )

    @Transactional
    fun register(input: RegisterLibraryUserInput): Result<RegisterLibraryUserResult, UsecaseError> =
        validateInput(input)
            .andThen(::verifyRegistration)
            .map(::createRegistrationContext)
            .map(::persistRegistration)
            .map(::buildRegisterResult)

    private fun validateInput(input: RegisterLibraryUserInput): Result<ValidatedInput, UsecaseError> =
        zip(
            { Email.create(input.email) },
            { authCredentialProvisioner.validate(input.password) },
        ) { email, password ->
            ValidatedInput(email = email, password = password)
        }.mapError(UsecaseError::fromDomain)

    private fun verifyRegistration(validated: ValidatedInput): Result<ValidatedInput, UsecaseError> =
        registrationService
            .verify(validated.email)
            .mapError(UsecaseError::fromDomain)
            .map { validated }

    private fun createRegistrationContext(validated: ValidatedInput): RegistrationContext {
        val libraryUserId = LibraryUserId.issue()
        val registeredAt = Instant.now(clock)
        val event =
            LibraryUserRegisteredEvent(
                libraryUserId = libraryUserId,
                email = validated.email,
                occurredAt = registeredAt,
            )

        return RegistrationContext(
            libraryUserId = libraryUserId,
            registeredAt = registeredAt,
            event = event,
            password = validated.password,
        )
    }

    private fun persistRegistration(context: RegistrationContext): RegistrationContext {
        libraryUserRegisteredEventHandler.handle(context.event)
        authCredentialProvisioner.provision(
            libraryUserId = context.libraryUserId.value,
            password = context.password,
        )
        return context
    }

    private fun buildRegisterResult(context: RegistrationContext): RegisterLibraryUserResult {
        val event = context.event
        return RegisterLibraryUserResult(
            libraryUserId = context.libraryUserId.value,
            email = event.email.value,
            registeredAt = context.registeredAt,
            eventName = event::class.simpleName ?: "LibraryUserRegisteredEvent",
        )
    }
}

data class RegisterLibraryUserInput(
    val email: String,
    val password: String,
)

data class RegisterLibraryUserResult(
    val libraryUserId: String,
    val email: String,
    val registeredAt: Instant,
    val eventName: String,
)
