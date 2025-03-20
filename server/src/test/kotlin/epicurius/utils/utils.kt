package epicurius.utils

import epicurius.EpicuriusTest.Companion.usersDomain
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import java.util.*

fun createTestUser(tm: TransactionManager, fs: FirestoreManager, privacy: Boolean): UserTest {
    val username = "test${Math.random()}"
    val email = "$username@email.com"
    val country = "PT"
    val password = UUID.randomUUID().toString()
    val passwordHash = usersDomain.encodePassword(password)

    tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
    fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    return UserTest(username, email, password)
}