import epicurius.domain.PagingParams
import epicurius.domain.user.SocialUser
import epicurius.domain.user.UpdateUserInfo
import epicurius.domain.user.User

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun getUsers(username: String, pagingParams: PagingParams): List<SocialUser>
    fun getFollowers(userId: Int): List<SocialUser>
    fun getFollowing(userId: Int): List<SocialUser>
    fun getFollowRequests(userId: Int): List<SocialUser>

    fun followUser(userId: Int, userIdToFollow: Int, status: Int)
    fun unfollowUser(userId: Int, userIdToUnfollow: Int)
    fun resetPassword(email: String, passwordHash: String)
    fun updateUser(username: String, userUpdate: UpdateUserInfo): User

    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
    fun checkIfUserIsBeingFollowedBy(userId: Int, userIdToFollow: Int): Boolean
}
