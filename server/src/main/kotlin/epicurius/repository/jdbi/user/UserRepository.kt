package epicurius.repository.jdbi.user

import epicurius.domain.PagingParams
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.domain.user.UpdateUserModel
import epicurius.domain.user.User

interface UserRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun getUsers(partialUsername: String, pagingParams: PagingParams): List<SearchUserModel>
    fun getFollowers(userId: Int): List<SearchUserModel>
    fun getFollowing(userId: Int): List<SearchUserModel>
    fun getFollowRequests(userId: Int): List<SearchUserModel>

    fun updateUser(username: String, userUpdate: UpdateUserModel): User
    fun resetPassword(email: String, passwordHash: String)
    fun followUser(userId: Int, userIdToFollow: Int, status: Int)
    fun unfollowUser(userId: Int, userIdToUnfollow: Int)
    fun cancelFollowRequest(userId: Int, followerId: Int)

    fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null): Boolean
    fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int): Boolean
    fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int): Boolean
}
