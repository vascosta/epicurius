package epicurius.unit.repository.user

import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserRepositoryTests : UserRepositoryTest() {

    val publicTestUser = createTestUser(tm)

    @Test
    fun `Should retrieve an user by id, name, email and token hash successfully`() {
        // given an existing user (publicTestUser)

        // when retrieving the user by id
        val userById = getUserById(publicTestUser.user.id)

        // when retrieving the user by name
        val userByName = getUserByName(publicTestUser.user.name)

        // when retrieving the user by email
        val userByEmail = getUserByEmail(publicTestUser.user.email)

        // when retrieving the user by token hash
        val userExistsByTokenHash = getUserByTokenHash(publicTestUser.user.tokenHash!!)

        // then the user is retrieved successfully
        assertNotNull(userById)
        assertNotNull(userByName)
        assertNotNull(userByEmail)
        assertNotNull(userExistsByTokenHash)
        assertEquals(publicTestUser.user.name, userById.name)
        assertEquals(publicTestUser.user.name, userByName.name)
        assertEquals(publicTestUser.user.name, userByEmail.name)
        assertEquals(publicTestUser.user.name, userExistsByTokenHash.name)
        assertEquals(publicTestUser.user.email, userById.email)
        assertEquals(publicTestUser.user.email, userByName.email)
        assertEquals(publicTestUser.user.email, userByEmail.email)
        assertEquals(publicTestUser.user.email, userExistsByTokenHash.email)
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
