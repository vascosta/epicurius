package epicurius.unit.repository.user

import epicurius.domain.PagingParams
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser

open class UserRepositoryTest : RepositoryTest() {

    companion object {
        val publicTestUser = createTestUser(tm)
        val privateTestUser = createTestUser(tm, false)

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(email = email) }
        fun getUserByTokenHash(tokenHash: String) = tm.run { it.userRepository.getUser(tokenHash = tokenHash) }

        fun searchUsers(userId: Int, partialUsername: String, pagingParams: PagingParams) =
            tm.run { it.userRepository.searchUsers(userId, partialUsername, pagingParams) }

        fun getFollowers(userId: Int) = tm.run { it.userRepository.getFollowers(userId) }
        fun getFollowing(userId: Int) = tm.run { it.userRepository.getFollowing(userId) }
        fun getFollowRequests(userId: Int) = tm.run { it.userRepository.getFollowRequests(userId) }

        fun updateUser(username: String, userUpdate: JdbiUpdateUserModel) =
            tm.run {
                it.userRepository.updateUser(
                    username,
                    JdbiUpdateUserModel(
                        userUpdate.name,
                        userUpdate.email,
                        userUpdate.country,
                        userUpdate.passwordHash,
                        userUpdate.privacy,
                        userUpdate.intolerances,
                        userUpdate.diets
                    )
                )
            }

        fun resetPassword(email: String, passwordHash: String) =
            tm.run { it.userRepository.resetPassword(email, passwordHash) }

        fun follow(userId: Int, userIdToFollow: Int, status: Int) =
            tm.run { it.userRepository.follow(userId, userIdToFollow, status) }

        fun unfollow(userId: Int, userIdToUnfollow: Int) =
            tm.run { it.userRepository.unfollow(userId, userIdToUnfollow) }

        fun cancelFollowRequest(userId: Int, userIdToCancelFollowRequest: Int) =
            tm.run { it.userRepository.cancelFollowRequest(userId, userIdToCancelFollowRequest) }

        fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null) =
            tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }

        fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }

        fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserAlreadySentFollowRequest(userId, followerId) }

        fun checkUserVisibility(authorUsername: String, username: String) =
            tm.run { it.userRepository.checkUserVisibility(authorUsername, username) }
    }
}
