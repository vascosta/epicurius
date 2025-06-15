package epicurius.unit.repository.user

import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.unit.repository.RepositoryTest

open class UserRepositoryTest : RepositoryTest() {

    companion object {

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun getUserById(userId: Int) = tm.run { it.userRepository.getUserById(userId) }
        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(email = email) }
        fun getUserByTokenHash(tokenHash: String) = tm.run { it.userRepository.getUser(tokenHash = tokenHash) }
        fun getUserProfilePictureName(userId: Int) = tm.run { it.userRepository.getUserProfilePictureName(userId) }

        fun searchUsers(
            userId: Int,
            partialUsername: String,
            lastUserId: Int?,
            limit: Int
        ) =
            tm.run { it.userRepository.searchUsers(userId, partialUsername, lastUserId, limit) }

        fun getFollowers(userId: Int, lastFollowerId: Int?, limit: Int) =
            tm.run { it.userRepository.getFollowers(userId, lastFollowerId, limit) }

        fun getFollowersCount(userId: Int) = tm.run { it.userRepository.getFollowersCount(userId) }

        fun getFollowing(userId: Int, lastFollowingId: Int?, limit: Int) =
            tm.run { it.userRepository.getFollowing(userId, lastFollowingId, limit) }

        fun getFollowingCount(userId: Int) = tm.run { it.userRepository.getFollowingCount(userId) }

        fun getFollowRequests(userId: Int) = tm.run { it.userRepository.getFollowRequests(userId) }

        fun updateUser(userId: Int, userUpdate: JdbiUpdateUserModel) =
            tm.run {
                it.userRepository.updateUser(
                    userId,
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

        fun resetPassword(userId: Int, passwordHash: String) =
            tm.run { it.userRepository.resetPassword(userId, passwordHash) }

        fun follow(userId: Int, userIdToFollow: Int, status: Int) =
            tm.run { it.userRepository.follow(userId, userIdToFollow, status) }

        fun unfollow(userId: Int, userIdToUnfollow: Int) =
            tm.run { it.userRepository.unfollow(userId, userIdToUnfollow) }

        fun deleteUser(userId: Int) = tm.run { it.userRepository.deleteUser(userId) }

        fun cancelFollowRequest(userId: Int, userIdToCancelFollowRequest: Int) =
            tm.run { it.userRepository.cancelFollowRequest(userId, userIdToCancelFollowRequest) }

        fun acceptFollowRequest(userId: Int, userIdToAcceptFollowRequest: Int) =
            tm.run { it.userRepository.acceptFollowRequest(userId, userIdToAcceptFollowRequest) }

        fun rejectFollowRequest(userId: Int, userIdToRejectFollowRequest: Int) =
            tm.run { it.userRepository.rejectFollowRequest(userId, userIdToRejectFollowRequest) }

        fun checkIfUserIsLoggedIn(userId: Int) =
            tm.run { it.userRepository.checkIfUserIsLoggedIn(userId) }

        fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }

        fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserAlreadySentFollowRequest(userId, followerId) }

        fun checkUserVisibility(authorUsername: String, userId: Int) =
            tm.run { it.userRepository.checkUserVisibility(authorUsername, userId) }
    }
}
