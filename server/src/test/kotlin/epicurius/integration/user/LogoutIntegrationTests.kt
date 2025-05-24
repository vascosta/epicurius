package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertTrue

class LogoutIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should logout a user successfully with code 204`() {
        // given a logged-in user
        val user = createTestUser(tm)

        // when logging out
        val cookieHeader = logout(user.token)

        // then the user is logged out successfully
        assertTrue(cookieHeader.isEmpty())
    }
}
