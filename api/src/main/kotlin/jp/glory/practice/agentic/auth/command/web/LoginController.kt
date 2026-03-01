package jp.glory.practice.agentic.auth.command.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.michaelbull.result.fold
import jp.glory.practice.agentic.auth.command.usecase.LoginInput
import jp.glory.practice.agentic.auth.command.usecase.LoginUseCase
import jp.glory.practice.agentic.shared.web.toApiException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth/login")
class LoginController(
    private val validator: LoginRequestValidator,
    private val useCase: LoginUseCase,
) {
    @PostMapping
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        validator.validateOrThrow(request)

        return useCase.login(
            LoginInput(
                email = request.email!!,
                password = request.password!!,
            )
        ).fold(
            success = {
                LoginResponse(
                    accessToken = it.accessToken,
                    tokenType = it.tokenType,
                    expiresInSeconds = it.expiresInSeconds,
                )
            },
            failure = { throw toApiException(it) },
        )
    }
}

data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
)

data class LoginResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in_seconds")
    val expiresInSeconds: Long,
)
