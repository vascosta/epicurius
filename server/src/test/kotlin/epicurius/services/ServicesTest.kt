package epicurius.services

import epicurius.repository.EpicuriusTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll

open class ServicesTest: EpicuriusTest() {

    companion object {
        val userService = UserService(tm, fs, usersDomain, countriesDomain)

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
        fun getUserByName(username: String) = userService.getUser(username, null)
        fun getUserByEmail(email: String) = userService.getUser(null, email)
        fun follow(username: String, usernameToFollow: String) = userService.follow(username, usernameToFollow)

    }
}