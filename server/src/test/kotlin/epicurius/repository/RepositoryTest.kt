package epicurius.repository

import epicurius.EpicuriusTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll

open class RepositoryTest: EpicuriusTest() {

    companion object {
        lateinit var publicTestUser: Pair<String, String>
        lateinit var privateTestUser: Pair<String, String>

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm, fs, false)
            privateTestUser = createTestUser(tm, fs, true)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username, null) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(null, email) }

        fun createUserFollowersAndFollowing(username: String, privacy: Boolean) =
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    }
}