package epicurius.repository

import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.junit.jupiter.api.BeforeAll
import java.util.UUID

open class RepositoryTest: EpicuriusTest() {

    companion object {
        private val tm = JdbiTransactionManager(jdbi)
        private val fs = FirestoreManager(firestore)

        lateinit var testUser: Pair<String, String>
        lateinit var testUser2: Pair<String, String>

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            testUser = createTestUser(false)
            testUser2 = createTestUser(true)
        }

        private fun createTestUser(privacy: Boolean): Pair<String, String> {
            val username = "test${Math.random()}"
            val email = "$username@email.com"
            val country = "PT"
            val passwordHash = usersDomain.encodePassword(UUID.randomUUID().toString())

            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

            return Pair(username, email)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username, null) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(null, email) }

        fun createUserFollowersAndFollowing(username: String, privacy: Boolean) =
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    }
}