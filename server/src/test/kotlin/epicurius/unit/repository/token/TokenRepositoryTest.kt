package epicurius.unit.repository.token

import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertNotNull

class TokenRepositoryTest : RepositoryTest() {

    companion object {
        private val testUser = createTestUser(tm)

        fun createToken(tokenHash: String, lastUsed: LocalDate, userId: Int) =
            tm.run { it.tokenRepository.createToken(tokenHash, lastUsed, userId) }

        fun deleteToken(userId: Int) =
            tm.run { it.tokenRepository.deleteToken(userId) }
    }

    @Test
    fun `Should create a token for an user and then delete the token successfully`() {
        // given an existing user (testUser)

        // when creating a token for the user
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        val lastUsed = LocalDate.now()
        createToken(tokenHash, lastUsed, testUser.id)

        // then the token is created successfully
        assertNotNull(tokenHash)

        // when deleting the token
        // then the token is deleted successfully
        deleteToken(testUser.id)
    }
}
