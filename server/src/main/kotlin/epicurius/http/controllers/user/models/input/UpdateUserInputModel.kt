package epicurius.http.controllers.user.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MAX_USERNAME_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MIN_USERNAME_LENGTH
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserInputModel(
    @field:Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = UserDomain.USERNAME_LENGTH_MSG)
    @field:Pattern(regexp = UserDomain.VALID_STRING, message = UserDomain.VALID_STRING_MSG)
    val name: String? = null,

    @field:Email(message = UserDomain.VALID_EMAIL_MSG)
    val email: String? = null,

    @field:Pattern(regexp = UserDomain.VALID_STRING, message = UserDomain.VALID_STRING_MSG)
    val country: String? = null,

    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = UserDomain.VALID_PASSWORD, message = UserDomain.VALID_PASSWORD_MSG)
    val password: String? = null,

    @field:Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = UserDomain.VALID_PASSWORD, message = UserDomain.VALID_PASSWORD_MSG)
    val confirmPassword: String? = null,

    val privacy: Boolean? = null,

    val intolerances: Set<Intolerance>? = null,

    val diets: Set<Diet>? = null
) {
    init {
        if (intolerances != null && intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }
    }

    fun toJdbiUpdateUser(passwordHash: String?) =
        JdbiUpdateUserModel(
            name, email, country, passwordHash, privacy, intolerances?.map { it.ordinal }, diets?.map { it.ordinal }
        )
}
