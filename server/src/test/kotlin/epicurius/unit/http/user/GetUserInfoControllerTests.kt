package epicurius.unit.http.user

import epicurius.http.controllers.user.models.output.GetUserOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserInfoControllerTests : UserHttpTest() {

    @Test
    fun `Should retrieve an authenticated user info successfully`() {
        // given an authenticated user (publicTestUser)

        // when retrieving the user info
        val response = getUserInfo(publicTestUser)
        val body = response.body as GetUserOutputModel

        // then the user info is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUser.user.toUserInfo(), body.userInfo)
    }
}
