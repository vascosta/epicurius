package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserInfoIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should retrieve an authenticated user info successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the user info
        val body = getUserInfo(user.token)

        // then the user info is retrieved successfully
        assertNotNull(body)
        assertEquals(user.user.toUserInfo(), body.userInfo)
    }
}