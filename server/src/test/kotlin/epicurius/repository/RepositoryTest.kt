package epicurius.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import epicurius.Environment
import epicurius.domain.UserDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.repository.firestore.FirestoreUserRepository
import epicurius.repository.jdbi.JdbiUserRepository
import epicurius.repository.jdbi.utils.configure
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
            testUser = createUser(false)
            testUser2 = createUser(true)
        }


        private fun createUser(privacy: Boolean): Pair<String, String> {
            val username = "test${Math.random()}"
            val email = "$username@email.com"
            val country = "PT"
            val passwordHash = usersDomain.encodePassword(UUID.randomUUID().toString())

            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

            return Pair(username, email)
        }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username, null) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(null, email) }

        fun createUserFollowersAndFollowing(username: String, privacy: Boolean) =
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    }
}