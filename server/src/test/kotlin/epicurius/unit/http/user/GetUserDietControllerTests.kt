package epicurius.unit.http.user

import epicurius.http.controllers.user.models.output.GetUserDietsOutputModel
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserDietControllerTests : UserControllerTest() {

    @Test
    fun `Should retrieve the diets of an user successfully`() {
        // given a user (publicTestUser)

        // when retrieving the user's diets
        val response = getUserDiet(publicTestUser)
        val body = response.body as GetUserDietsOutputModel

        // then the user's diets are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUser.user.toUserInfo().diets, body.diets)
    }
}
