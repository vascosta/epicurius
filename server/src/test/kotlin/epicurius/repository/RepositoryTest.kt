package epicurius.repository

import epicurius.EpicuriusTest
import epicurius.domain.PagingParams
import epicurius.domain.user.UpdateUserInfo
import epicurius.domain.user.User
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.web.multipart.MultipartFile

open class RepositoryTest : EpicuriusTest() {

    companion object {
        lateinit var publicTestUser: User
        lateinit var privateTestUser: User

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun createToken(tokenHash: String, username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.createToken(tokenHash, username, email) }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(email = email) }
        fun getUserByTokenHash(tokenHash: String) = tm.run { it.userRepository.getUser(tokenHash = tokenHash) }

        fun getUsers(partialUsername: String, pagingParams: PagingParams) =
            tm.run { it.userRepository.getUsers(partialUsername, pagingParams) }

        fun getProfilePicture(profilePictureName: String) = cs.userCloudStorageRepository.getProfilePicture(profilePictureName)

        fun getFollowers(userId: Int) = tm.run { it.userRepository.getFollowers(userId) }
        fun getFollowing(userId: Int) = tm.run { it.userRepository.getFollowing(userId) }
        fun getFollowRequests(userId: Int) = tm.run { it.userRepository.getFollowRequests(userId) }

        fun updateUser(username: String, userUpdate: UpdateUserInfo) =
            tm.run {
                it.userRepository.updateUser(
                    username,
                    UpdateUserInfo(
                        userUpdate.username,
                        userUpdate.email,
                        userUpdate.country,
                        userUpdate.passwordHash,
                        userUpdate.privacy,
                        userUpdate.intolerances,
                        userUpdate.diet
                    )
                )
            }

        fun updateProfilePicture(profilePictureName: String, profilePicture: MultipartFile) =
            cs.userCloudStorageRepository.updateProfilePicture(profilePictureName, profilePicture)

        fun resetPassword(email: String, passwordHash: String) =
            tm.run { it.userRepository.resetPassword(email, passwordHash) }

        fun follow(userId: Int, userIdToFollow: Int, status: Int) =
            tm.run { it.userRepository.followUser(userId, userIdToFollow, status) }

        fun unfollow(userId: Int, userIdToUnfollow: Int) =
            tm.run { it.userRepository.unfollowUser(userId, userIdToUnfollow) }

        fun deleteToken(username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.deleteToken(username, email) }

        fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null) =
            tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }

        fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }
    }
}
