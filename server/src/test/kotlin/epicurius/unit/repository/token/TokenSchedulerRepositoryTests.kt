package epicurius.unit.repository.token

import epicurius.domain.user.User
import epicurius.repository.jdbi.token.TokenScheduler
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID.randomUUID
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TokenSchedulerRepositoryTests : RepositoryTest() {

    companion object {
        private fun createTestUserWithInvalidToken(invalidDate: LocalDate): User {
            val username = generateRandomUsername()
            val email = generateEmail(username)
            val country = "PT"
            val password = generateSecurePassword()
            val passwordHash = userDomain.encodePassword(password)

            val userId = tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

            val tokenHash = userDomain.hashToken(randomUUID().toString())
            tm.run { it.tokenRepository.createToken(tokenHash, invalidDate, userId) }

            return tm.run { it.userRepository.getUser(username) } ?: throw Exception("User not created")
        }

        private val tokenScheduler = TokenScheduler(jdbi)
    }

    @Test
    fun `Should delete token older than 30 days`() {
        // given a date older than 30 days
        val lastUsed = LocalDate.now().minusDays(31)

        // when creating a user with token older than 30 days
        val userWithToken = createTestUserWithInvalidToken(lastUsed)

        // then the token should be created successfully
        assertNotNull(userWithToken)
        assertNotNull(userWithToken.tokenHash)

        // when the scheduler is triggered
        tokenScheduler.scheduleDeleteToken()

        // then the token should be deleted
        val userWithNoToken = tm.run { it.userRepository.getUser(userWithToken.name) }
        assertNotNull(userWithNoToken)
        assertNull(userWithNoToken.tokenHash)
    }

    @Test
    fun `Should not delete token younger than 30 days`() {
        // given a date younger than 30 days
        val lastUsed = LocalDate.now().minusDays(29)

        // when creating a user with token younger than 30 days
        val userWithToken = createTestUserWithInvalidToken(lastUsed)

        // then the token should be created successfully
        assertNotNull(userWithToken)
        assertNotNull(userWithToken.tokenHash)

        // when the scheduler is triggered
        tokenScheduler.scheduleDeleteToken()

        // then the token should not be deleted
        val userWithNoToken = tm.run { it.userRepository.getUser(userWithToken.name) }
        assertNotNull(userWithNoToken)
        assertNotNull(userWithNoToken.tokenHash)
    }
}
