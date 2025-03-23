package epicurius.repository

import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenRepositoryTest: RepositoryTest() {

    @Test
    fun `Creates a token for an user, retrieves it by token hash and then deletes the token successfully`() {
        // given an existing user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())
        createUser(username, email, country, passwordHash)

        // when creating a token for the user
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, username)

        // when retrieving the user by the token hash
        val userFromTokenHash = getUserByTokenHash(tokenHash)

        // then the user is retrieved successfully
        assertNotNull(userFromTokenHash)
        assertEquals(username, userFromTokenHash.username)
        assertEquals(email, userFromTokenHash.email)

        // when deleting the token
        deleteToken(username)

        // then the token is deleted successfully
        assertNull(getUserByTokenHash(tokenHash))
    }
}