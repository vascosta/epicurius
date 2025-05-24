package epicurius.unit.repository.user

import epicurius.domain.PagingParams
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateSecurePassword
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

        // when retrieving the users by a partial username
        val users = searchUsers(publicTestUser.user.id, "partial", PagingParams(limit = 2))

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertTrue(users.contains(SearchUserModel(username, null)))
        assertTrue(users.contains(SearchUserModel(username2, null)))
        assertFalse(users.contains(SearchUserModel(publicTestUser.user.name, publicTestUser.user.profilePictureName)))
    }
}
