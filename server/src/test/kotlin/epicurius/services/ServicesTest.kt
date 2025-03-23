package epicurius.services

import epicurius.EpicuriusTest
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.utils.UserTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll

open class ServicesTest: EpicuriusTest() {

    companion object {
        private val userService = UserService(tm, fs, usersDomain, countriesDomain)

        lateinit var publicTestUser: UserTest
        lateinit var privateTestUser: UserTest

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            userService.createUser(username, email, country, passwordHash)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun login(username: String?, email: String?, password: String) = userService.login(username, email, password)

        fun follow(userId: Int, usernameToFollow: String) = userService.follow(userId, usernameToFollow)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun updateProfile(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateProfile(username, userUpdate)

        fun logout(username: String) = userService.logout(username)
    }
}