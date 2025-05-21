package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SearchUsersIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should search for users and retrieve them successfully with code 200`() {
        // given two users with their names containing a common string and a user searching for them
        val user = createTestUser(tm)
        repeat(2) { createTestUser(tm) }

        // when retrieving the users by a common string
        val body = searchUsers(user.token, "test")

        // then the users are retrieved successfully with code 200
        assertNotNull(body)
        assertEquals(2, body.users.size)
    }
}