package epicurius.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenRepositoryTest: RepositoryTest() {

    @Test
    fun `Creates a token for an user and then retrieves it by token hash successfully`() {
        // given an existing user
        val user = publicTestUser

        // when creating a token for the user
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, user.username)

        // when retrieving the user by the token hash
        val userFromTokenHash = getUserByTokenHash(tokenHash)

        //then the user is retrieved successfully
        assertEquals(user.username, userFromTokenHash.username)
        assertEquals(user.email, userFromTokenHash.email)
        assertTrue(usersDomain.verifyPassword(user.password, userFromTokenHash.passwordHash))
    }
}