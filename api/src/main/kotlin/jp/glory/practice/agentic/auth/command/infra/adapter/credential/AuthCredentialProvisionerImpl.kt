package jp.glory.practice.agentic.auth.command.infra.adapter.credential

import com.github.michaelbull.result.map
import jp.glory.practice.agentic.auth.command.domain.model.AuthCredential
import jp.glory.practice.agentic.auth.command.domain.model.LibraryUserId
import jp.glory.practice.agentic.auth.command.domain.model.RawPassword
import jp.glory.practice.agentic.auth.command.domain.repository.AuthCredentialRepository
import jp.glory.practice.agentic.auth.command.domain.service.PasswordHasher
import jp.glory.practice.agentic.shared.auth.AuthCredentialProvisioner
import jp.glory.practice.agentic.shared.auth.ValidatedAuthPassword
import org.springframework.stereotype.Component

@Component
class AuthCredentialProvisionerImpl(
    private val authCredentialRepository: AuthCredentialRepository,
    private val passwordHasher: PasswordHasher,
) : AuthCredentialProvisioner {
    override fun validate(rawPassword: String) =
        RawPassword
            .create(rawPassword)
            .map { ValidatedAuthPassword(it.value) }

    override fun provision(
        libraryUserId: String,
        password: ValidatedAuthPassword,
    ) {
        authCredentialRepository.save(
            AuthCredential(
                libraryUserId = LibraryUserId(libraryUserId),
                passwordHash = passwordHasher.hash(password.value),
            ),
        )
    }
}
