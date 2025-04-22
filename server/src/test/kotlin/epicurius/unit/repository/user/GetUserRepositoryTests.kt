package epicurius.unit.repository.user

import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserRepositoryTests : UserRepositoryTest() {

    @Test
    fun `Should retrieve an user by name, email and token hash successfully`() {
        // given an existing user (publicTestUser)

        // when retrieving the user by name
        val userByName = getUserByName(publicTestUser.name)

        // when retrieving the user by email
        val userByEmail = getUserByEmail(publicTestUser.email)

        // when retrieving the user by token hash
        val userExistsByTokenHash = getUserByTokenHash(publicTestUser.tokenHash!!)

        // then the user is retrieved successfully
        assertNotNull(userByName)
        assertNotNull(userByEmail)
        assertNotNull(userExistsByTokenHash)
        assertEquals(publicTestUser.name, userByName.name)
        assertEquals(publicTestUser.name, userByEmail.name)
        assertEquals(publicTestUser.name, userExistsByTokenHash.name)
        assertEquals(publicTestUser.email, userByName.email)
        assertEquals(publicTestUser.email, userByEmail.email)
        assertEquals(publicTestUser.email, userExistsByTokenHash.email)
    }

    @Test
    fun `Should return null when retrieving an non-existing user by name, email and token hash`() {
        // given a non-existing username, email and token hash
        val username = ""
        val email = ""
        val tokenHash = ""

        // when retrieving the user by name
        val userByName = getUserByName(username)

        // when retrieving the user by email
        val userByEmail = getUserByEmail(email)

        // when retrieving the user by token hash
        val userByTokenHash = getUserByTokenHash(tokenHash)

        // then the user does not exist
        assertNull(userByName)
        assertNull(userByEmail)
        assertNull(userByTokenHash)
    }
}
