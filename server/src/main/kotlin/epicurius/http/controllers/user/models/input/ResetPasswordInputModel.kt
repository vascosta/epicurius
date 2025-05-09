package epicurius.http.controllers.user.models.input

import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_PASSWORD_LENGTH
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ResetPasswordInputModel(
    @field:NotBlank
    @field:Email(message = UserDomain.VALID_EMAIL_MSG)
    val email: String,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = UserDomain.PASSWORD_LENGTH_MSG)
    @field:Pattern(regexp = UserDomain.VALID_PASSWORD, message = UserDomain.VALID_PASSWORD_MSG)
    val newPassword: String,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = UserDomain.PASSWORD_LENGTH_MSG)
    @field:Pattern(regexp = UserDomain.VALID_PASSWORD, message = UserDomain.VALID_PASSWORD_MSG)
    val confirmPassword: String
)
