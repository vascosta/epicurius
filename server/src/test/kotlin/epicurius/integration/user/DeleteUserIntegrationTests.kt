package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteUserIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should delete a user successfully`() {
        // given an existing user
        val user = createTestUser(tm)

        // when deleting the user
        val cookieHeader = deleteUser(user.token)

        // then the user is deleted successfully
        assertTrue(cookieHeader.isEmpty())
    }
}