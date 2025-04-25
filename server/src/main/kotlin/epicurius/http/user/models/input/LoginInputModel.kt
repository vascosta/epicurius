package epicurius.http.user.models.input

import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_PASSWORD_LENGTH
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginInputModel(
    @field:Size(min = UserDomain.MIN_USERNAME_LENGTH, max = UserDomain.MAX_USERNAME_LENGTH, message = UserDomain.USERNAME_LENGTH_MSG)
    @field:Pattern(regexp = UserDomain.VALID_STRING, message = UserDomain.VALID_STRING_MSG)
    val name: String?,

    @field:Email(message = UserDomain.VALID_EMAIL_MSG)
    val email: String?,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = UserDomain.VALID_PASSWORD, message = UserDomain.VALID_PASSWORD_MSG)
    val password: String,
)
