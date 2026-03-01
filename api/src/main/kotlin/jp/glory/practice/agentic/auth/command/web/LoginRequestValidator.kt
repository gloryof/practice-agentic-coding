package jp.glory.practice.agentic.auth.command.web

import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import jp.glory.practice.agentic.shared.web.ApiErrorDetail
import jp.glory.practice.agentic.shared.web.ValidationApiException
import org.springframework.stereotype.Component

@Component
class LoginRequestValidator {
    private val halfWidthValidation = Validation<String> {
        pattern(Regex("^[\\u0020-\\u007E]+$"))
    }

    private val passwordValidation = Validation<String> {
        minLength(12)
        pattern(Regex(".*[A-Z].*"))
        pattern(Regex(".*[a-z].*"))
        pattern(Regex(".*[0-9].*"))
        pattern(Regex(".*[^A-Za-z0-9].*"))
    }

    fun validateOrThrow(request: LoginRequest) {
        val details = mutableListOf<ApiErrorDetail>()

        val email = request.email
        if (email.isNullOrBlank()) {
            details.add(ApiErrorDetail(field = "email", reason = "required"))
        } else if (!halfWidthValidation(email).isValid) {
            details.add(ApiErrorDetail(field = "email", reason = "must_be_half_width"))
        }

        val password = request.password
        if (password.isNullOrBlank()) {
            details.add(ApiErrorDetail(field = "password", reason = "required"))
        } else if (!passwordValidation(password).isValid) {
            details.add(ApiErrorDetail(field = "password", reason = "must_meet_password_policy"))
        }

        if (details.isNotEmpty()) {
            throw ValidationApiException(details)
        }
    }
}
