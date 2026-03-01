package jp.glory.practice.agentic.libraryuser.command.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.libraryuser.command.usecase.RegisterLibraryUserInput
import jp.glory.practice.agentic.libraryuser.command.usecase.RegisterLibraryUserUseCase
import jp.glory.practice.agentic.shared.web.toApiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/library-users/registrations")
class RegistrationController(
    private val validator: RegistrationRequestValidator,
    private val useCase: RegisterLibraryUserUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterLibraryUserRequest): RegisterLibraryUserResponse {
        validator.validateOrThrow(request)

        return useCase.register(
            RegisterLibraryUserInput(
                email = request.email!!,
                password = request.password!!,
            )
        ).fold(
            success = {
                RegisterLibraryUserResponse(
                    libraryUserId = it.libraryUserId,
                    email = it.email,
                    registeredAt = it.registeredAt,
                )
            },
            failure = { throw toApiException(it) },
        )
    }
}

data class RegisterLibraryUserRequest(
    val email: String? = null,
    val password: String? = null,
)

data class RegisterLibraryUserResponse(
    @JsonProperty("library_user_id")
    val libraryUserId: String,
    val email: String,
    @JsonProperty("registered_at")
    val registeredAt: Instant,
)
