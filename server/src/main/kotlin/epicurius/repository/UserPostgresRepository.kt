import epicurius.domain.user.User
import epicurius.services.models.UpdateUserModel

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null): User
    fun getUserFromTokenHash(tokenHash: String): User
    fun getProfilePictureName(username: String): String

    fun resetPassword(email: String, passwordHash: String)
    fun updateProfile(username:String, userUpdate: UpdateUserModel)

    fun checkIfUserExists(username: String? = null, email: String? = null, tokenHash: String? = null): Boolean
    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
}
