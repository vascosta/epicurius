package epicurius.unit.repository.user

import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateSecurePassword
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchUsersRepositoryTests : UserRepositoryTest() {

    val publicTestUser = createTestUser(tm)

    @Test
    fun `Should search for users and retrieve them successfully`() {
        // given two users with their names containing a common string and a user (publicTestUser) searching for them
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val passwordHash = userDomain.encodePassword(generateSecurePassword())
        createUser(username, email, country, passwordHash)
        createUser(username2, email2, country, passwordHash)
        val limit = 2

        // when retrieving the users by a partial username
        val users = searchUsers(publicTestUser.user.id, "partial", null, limit)

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertNotNull(users.firstOrNull { it.name == username })
        assertNotNull(users.firstOrNull { it.name == username2 })
        assertNull(users.firstOrNull { it.name == publicTestUser.user.name })
    }
}
