package epicurius.http.user.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MAX_USERNAME_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_USERNAME_LENGTH
import epicurius.http.utils.Regex
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserInputModel(
    @field:Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = UserDomain.USERNAME_LENGTH_MSG)
    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
    val username: String? = null,

    @field:Email(message = UserDomain.VALID_EMAIL_MSG)
    val email: String? = null,

    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
    val country: String? = null,

    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val password: String? = null,

    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = Regex.VALID_PASSWORD, message = Regex.VALID_PASSWORD_MSG)
    val confirmPassword: String? = null,

    val privacy: Boolean? = null,

    val intolerances: List<Intolerance>? = null,

    val diets: List<Diet>? = null
)
