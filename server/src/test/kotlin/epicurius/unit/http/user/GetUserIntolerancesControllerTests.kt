package epicurius.unit.http.user

import epicurius.http.user.models.output.GetUserIntolerancesOutputModel
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserIntolerancesControllerTests : UserHttpTest() {

    @Test
    fun `Should retrieve the intolerances of an user successfully`() {
        // given a user (publicTestUser)

        // when retrieving the user's intolerances
        val response = getUserIntolerances(publicTestUser)
        val body = response.body as GetUserIntolerancesOutputModel

        // then the user's intolerances are retrieved successfully
        assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
        assertEquals(publicTestUser.user.toUserInfo().intolerances, body.intolerances)
    }
}
