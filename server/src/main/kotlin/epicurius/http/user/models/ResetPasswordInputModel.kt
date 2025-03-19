package epicurius.http.user.models

import epicurius.domain.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.UserDomain.Companion.MIN_PASSWORD_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import epicurius.http.utils.Regex

data class ResetPasswordInputModel (
    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val newPassword: String,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val confirmPassword: String
)