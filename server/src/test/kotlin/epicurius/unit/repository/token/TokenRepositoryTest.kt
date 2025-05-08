package epicurius.unit.repository.token

import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TokenRepositoryTest : RepositoryTest() {

    companion object {
        private val testUser = createTestUser(tm)

        fun createToken(tokenHash: String, lastUsed: LocalDate, userId: Int) =
            tm.run { it.tokenRepository.createToken(tokenHash, lastUsed, userId) }

        fun deleteToken(userId: Int) =
            tm.run { it.tokenRepository.deleteToken(userId) }
    }

    @Test
    fun `Should delete a token for an user and then create a new one successfully`() {
        // given an existing user (testUser)

        // when deleting the token
        deleteToken(testUser.id)

        // then the token is deleted successfully
        val notFoundUser = tm.run { it.userRepository.getUser(tokenHash = testUser.tokenHash) }
        assertNull(notFoundUser)

        // when creating a new token
        val newToken = userDomain.generateTokenValue()
        val newTokenHash = userDomain.hashToken(newToken)
        val lastUsed = LocalDate.now()
        createToken(newTokenHash, lastUsed, testUser.id)

        // then the token is created successfully
        val userWithNewToken = tm.run { it.userRepository.getUser(tokenHash = newTokenHash) }
        assertNotNull(userWithNewToken)
    }
}
