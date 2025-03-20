package epicurius.services

import epicurius.EpicuriusTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll

open class ServicesTest: EpicuriusTest() {

    companion object {
        private val userService = UserService(tm, fs, usersDomain, countriesDomain)

        lateinit var publicTestUser: Pair<String, String>
        lateinit var privateTestUser: Pair<String, String>

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm, fs, false)
            privateTestUser = createTestUser(tm, fs, true)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            userService.createUser(username, email, country, passwordHash)
        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)
        fun login(username: String?, email: String?, password: String) = userService.login(username, email, password)
        fun logout(username: String) = userService.logout(username)
        fun follow(username: String, usernameToFollow: String) = userService.follow(username, usernameToFollow)

    }
}