package epicurius.repository.jdbi.user

import epicurius.domain.PagingParams
import epicurius.domain.user.SearchUserModel
import epicurius.domain.user.UpdateUserInfo
import epicurius.domain.user.User

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun getUsers(partialUsername: String, pagingParams: PagingParams): List<SearchUserModel>
    fun getFollowers(userId: Int): List<SearchUserModel>
    fun getFollowing(userId: Int): List<SearchUserModel>
    fun getFollowRequests(userId: Int): List<SearchUserModel>

    fun updateUser(username: String, userUpdate: UpdateUserInfo): User
    fun resetPassword(email: String, passwordHash: String)
    fun followUser(userId: Int, userIdToFollow: Int, status: Int)
    fun unfollowUser(userId: Int, userIdToUnfollow: Int)
    fun cancelFollowRequest(userId: Int, followerId: Int)

    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
    fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int): Boolean
    fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int): Boolean
}
