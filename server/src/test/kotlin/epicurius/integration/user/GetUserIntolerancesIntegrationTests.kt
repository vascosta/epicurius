package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserIntolerancesIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the intolerances of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the user's intolerances
        val body = getUserIntolerances(user.token)

        // then the user's intolerances are retrieved successfully
        assertNotNull(body)
        assertEquals(user.user.intolerances, body.intolerances)
    }
}
