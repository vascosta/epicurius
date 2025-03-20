package epicurius.utils

import epicurius.repository.EpicuriusTest
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import java.util.*

fun createTestUser(tm: TransactionManager, fs: FirestoreManager, privacy: Boolean): Pair<String, String> {
    val username = "test${Math.random()}"
    val email = "$username@email.com"
    val country = "PT"
    val passwordHash = EpicuriusTest.usersDomain.encodePassword(UUID.randomUUID().toString())

    tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
    fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    return Pair(username, email)
}