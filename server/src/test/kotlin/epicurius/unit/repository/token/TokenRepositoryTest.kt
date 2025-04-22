package epicurius.unit.repository.token

import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull

class TokenRepositoryTest : RepositoryTest() {

    companion object {
        private val testUser = createTestUser(tm)

        fun createToken(tokenHash: String, username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.createToken(tokenHash, username, email) }

        fun deleteToken(username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.deleteToken(username, email) }
    }

    @Test
    fun `Should create a token for an user and then delete the token successfully`() {
        // given an existing user (testUser)

        // when creating a token for the user
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        createToken(tokenHash, testUser.name)

        // then the token is created successfully
        assertNotNull(tokenHash)

        // when deleting the token
        // then the token is deleted successfully
        deleteToken(testUser.name)
    }
}
