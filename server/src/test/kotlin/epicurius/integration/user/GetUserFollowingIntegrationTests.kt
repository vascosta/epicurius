package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserFollowingIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the following of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the following of the user
        val body = getUserFollowing(user.token)

        // then the following are retrieved successfully
        assertNotNull(body)
        assertTrue(body.users.isEmpty())
    }
}
