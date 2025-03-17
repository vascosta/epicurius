package epicurius.http.user.models

import epicurius.domain.UserDomain
import epicurius.domain.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.UserDomain.Companion.MIN_PASSWORD_LENGTH
import epicurius.http.utils.Regex
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginInputModel(
    @field:Size(min = UserDomain.MIN_USERNAME_LENGTH, max = UserDomain.MAX_USERNAME_LENGTH)
    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
    val username: String?,

    @field:Email
    val email: String?,

    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val password: String,
)