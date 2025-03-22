package epicurius.services

import epicurius.EpicuriusTest
import epicurius.http.user.models.UpdateUserInputModel
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
            publicTestUser = createTestUser(tm, fs, false)
            privateTestUser = createTestUser(tm, fs, true)
            println(publicTestUser.username)
            println(privateTestUser.username)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            userService.createUser(username, email, country, passwordHash)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun login(username: String?, email: String?, password: String) = userService.login(username, email, password)

        fun follow(username: String, usernameToFollow: String) = userService.follow(username, usernameToFollow)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun updateProfile(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateProfile(username, userUpdate)

        fun logout(username: String) = userService.logout(username)
    }
}