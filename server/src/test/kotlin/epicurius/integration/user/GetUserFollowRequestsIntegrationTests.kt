package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserFollowRequestsIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should retrieve the follow requests of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the follow requests of the user
        val body = getUserFollowRequests(user.token)

        // then the follow requests are retrieved successfully
        assertNotNull(body)
        assertTrue(body.users.isEmpty())
    }
}