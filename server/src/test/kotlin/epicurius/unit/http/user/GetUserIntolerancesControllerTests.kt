package epicurius.unit.http.user

import epicurius.http.user.models.output.GetUserIntolerancesOutputModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserIntolerancesControllerTests: UserHttpTest() {

    @Test
    fun `Should retrieve the intolerances of an user successfully`() {
        // given a user (publicTestUser)

        // when retrieving the user's intolerances
        val body = getUserIntolerances(publicTestUser).body as GetUserIntolerancesOutputModel

        // then the user's intolerances are retrieved successfully
        assertNotNull(body)
        assertEquals(publicTestUser.user.toUserInfo().intolerances, body.intolerances)
    }
}