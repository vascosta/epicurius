package epicurius.unit.http.user

import epicurius.http.user.models.output.GetUserDietsOutputModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserDietControllerTests: UserHttpTest() {

    @Test
    fun `Should retrieve the diets of an user successfully`() {
        // given a user (publicTestUser)

        // when retrieving the user's diets
        val body = getUserDiet(publicTestUser).body as GetUserDietsOutputModel

        // then the user's diets are retrieved successfully
        assertNotNull(body)
        assertEquals(publicTestUser.user.toUserInfo().diets, body.diets)
    }
}