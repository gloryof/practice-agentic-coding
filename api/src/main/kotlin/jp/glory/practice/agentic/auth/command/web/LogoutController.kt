package jp.glory.practice.agentic.auth.command.web

import jp.glory.practice.agentic.auth.command.usecase.LogoutInput
import jp.glory.practice.agentic.auth.command.usecase.LogoutUseCase
import jp.glory.practice.agentic.shared.auth.AccessTokenAuthenticator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth/logout")
class LogoutController(
    private val authenticator: AccessTokenAuthenticator,
    private val useCase: LogoutUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        @RequestHeader("Authorization", required = false) authorization: String?,
    ) {
        val session = authenticator.requireValidToken(authorization)
        useCase.logout(
            LogoutInput(
                libraryUserId = session.libraryUserId,
                accessToken = session.token,
            ),
        )
    }
}
