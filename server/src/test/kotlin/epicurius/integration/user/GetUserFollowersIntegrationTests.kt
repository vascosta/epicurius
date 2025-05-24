package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserFollowersIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the followers of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the followers of the user
        val body = getUserFollowers(user.token)

        // then the followers are retrieved successfully
        assertNotNull(body)
        assertTrue(body.users.isEmpty())
    }
}
