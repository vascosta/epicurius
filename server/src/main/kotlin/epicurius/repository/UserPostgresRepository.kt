import epicurius.domain.user.User
import epicurius.services.models.UpdateUserModel

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun getProfilePictureName(username: String): String

    fun followUser(userId: Int, userIdToFollow: Int)
    fun resetPassword(email: String, passwordHash: String)
    fun updateProfile(username:String, userUpdate: UpdateUserModel)

    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
    fun checkIfUserIsAlreadyFollowing(userId: Int, userIdToFollow: Int): Boolean
}
