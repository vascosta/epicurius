package epicurius.utils

import epicurius.EpicuriusTest.Companion.usersDomain
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.repository.transaction.TransactionManager
import java.util.UUID

fun createTestUser(tm: TransactionManager): User {
    val username = generateRandomUsername()
    val email = generateEmail(username)
    val country = "PT"
    val password = generateSecurePassword()
    val passwordHash = usersDomain.encodePassword(password)

    tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

    return tm.run { it.userRepository.getUser(username) } ?: throw Exception("User not created")
}

fun generateRandomUsername() = "test${Math.random()}".replace(".", "")
fun generateEmail(username: String) = "$username@email.com"

fun generateSecurePassword() = ("P" + UUID.randomUUID().toString()).take(MAX_PASSWORD_LENGTH)
