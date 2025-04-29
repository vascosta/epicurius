package epicurius.repository.jdbi.user.contract

import epicurius.domain.PagingParams
import epicurius.domain.user.User
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.jdbi.user.models.SearchUserModel

interface UserRepository {

    fun createUser(name: String, email: String, country: String, passwordHash: String)

    fun getUser(name: String? = null, email: String? = null, tokenHash: String? = null): User?
    fun searchUsers(userId: Int, partialUsername: String, pagingParams: PagingParams): List<SearchUserModel>
    fun getFollowers(userId: Int): List<SearchUserModel>
    fun getFollowing(userId: Int): List<SearchUserModel>
    fun getFollowRequests(userId: Int): List<SearchUserModel>

    fun updateUser(name: String, userUpdateInfo: JdbiUpdateUserModel): User
    fun resetPassword(email: String, passwordHash: String)
    fun follow(userId: Int, userIdToFollow: Int, status: Int)
    fun unfollow(userId: Int, userIdToUnfollow: Int)
    fun cancelFollowRequest(userId: Int, followerId: Int)

    fun checkIfUserIsLoggedIn(name: String? = null, email: String? = null): Boolean
    fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int): Boolean
    fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int): Boolean
    fun checkRecipeAccessibility(authorUsername: String, authorId: Int, username: String): Boolean
}
