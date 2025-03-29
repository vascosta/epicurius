package epicurius.services

import epicurius.EpicuriusTest
import epicurius.domain.PagingParams
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.web.multipart.MultipartFile

open class ServicesTest : EpicuriusTest() {

    companion object {
        // private val userService = UserService(tm, fs, cs, usersDomain, countriesDomain)
        private val userService = UserService(tm, cs, usersDomain, countriesDomain)

        lateinit var publicTestUser: User
        lateinit var privateTestUser: User

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm, true)
        }

        fun createUser(username: String, email: String, country: String, password: String, confirmPassword: String) =
            userService.createUser(username, email, country, password, confirmPassword)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun getUserProfile(username: String) = userService.getUserProfile(username)

        fun getProfilePicture(profilePictureName: String) = userService.getProfilePicture(profilePictureName)

        fun getUsers(partialUsername: String, pagingParams: PagingParams) = userService.getUsers(partialUsername, pagingParams)

        fun getFollowers(userId: Int) = userService.getFollowers(userId)

        fun getFollowing(userId: Int) = userService.getFollowing(userId)

        fun getFollowRequests(userId: Int) = userService.getFollowRequests(userId)

        fun login(username: String? = null, email: String? = null, password: String) =
            userService.login(username, email, password)

        fun logout(username: String) = userService.logout(username)

        fun updateUser(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateUser(username, userUpdate)

        fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile) =
            userService.updateProfilePicture(username, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun follow(userId: Int, usernameToFollow: String) = userService.follow(userId, usernameToFollow)

        fun unfollow(userId: Int, usernameToUnfollow: String) = userService.unfollow(userId, usernameToUnfollow)

        fun cancelFollowRequest(userId: Int, usernameToCancelFollow: String) =
            userService.cancelFollowRequest(userId, usernameToCancelFollow)
    }
}
