package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserDietsIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the diets of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the user's diets
        val body = getUserDiets(user.token)

        // then the user's diets are retrieved successfully
        assertNotNull(body)
        assertEquals(user.user.diets, body.diets)
    }
}
