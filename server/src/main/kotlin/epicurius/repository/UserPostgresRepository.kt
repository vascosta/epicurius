import epicurius.domain.user.SocialUser
import epicurius.domain.user.User
import epicurius.domain.user.UserProfile
import epicurius.services.models.UpdateUserModel

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun getFollowers(userId: Int): List<SocialUser>
    fun getFollowing(userId: Int): List<SocialUser>
    fun getFollowRequests(userId: Int): List<SocialUser>

    fun followUser(userId: Int, userIdToFollow: Int, status: Int)
    fun resetPassword(email: String, passwordHash: String)
    fun updateProfile(username:String, userUpdate: UpdateUserModel): User

    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
    fun checkIfUserIsAlreadyFollowing(userId: Int, userIdToFollow: Int): Boolean
}
