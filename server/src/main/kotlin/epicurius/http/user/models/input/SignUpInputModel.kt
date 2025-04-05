package epicurius.http.user.models.input

import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MAX_USERNAME_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_USERNAME_LENGTH
import epicurius.http.utils.Regex
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpInputModel(
    @field:NotBlank
    @field:Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = UserDomain.USERNAME_LENGTH_MSG)
    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
    val username: String,

    @field:NotBlank
    @field:Email(message = UserDomain.VALID_EMAIL_MSG)
    val email: String,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = UserDomain.PASSWORD_LENGTH_MSG)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val password: String,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = UserDomain.PASSWORD_LENGTH_MSG)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val confirmPassword: String,

    @field:NotBlank
    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
    val country: String
)
