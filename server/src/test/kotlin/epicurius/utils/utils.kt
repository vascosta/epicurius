package epicurius.utils

import epicurius.EpicuriusTest.Companion.usersDomain
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import java.util.*

fun createTestUser(tm: TransactionManager, fs: FirestoreManager, privacy: Boolean): UserTest {
    val username = generateRandomUsername()
    val email = generateEmail(username)
    val country = "PT"
    val password = generateSecurePassword()
    val passwordHash = usersDomain.encodePassword(password)

    tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
    fs.userRepository.createUserFollowersAndFollowing(username, privacy)

    return UserTest(username, email, password)
}

fun generateRandomUsername() = "test${Math.random()}".replace(".", "")
fun generateEmail(username: String) = "$username@email.com"

fun generateSecurePassword() = ("P" + UUID.randomUUID().toString()).take(MAX_PASSWORD_LENGTH)