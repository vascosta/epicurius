package epicurius.unit.http.user

import epicurius.http.user.models.output.GetUserOutputModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserInfoControllerTests : UserHttpTest() {

    @Test
    fun `Should retrieve an authenticated user info successfully`() {
        // given an authenticated user (publicTestUser)

        // when retrieving the user info
        val body = getUserInfo(publicTestUser).body as GetUserOutputModel

        // then the user info is retrieved successfully
        assertNotNull(body)
        assertEquals(publicTestUser.user.toUserInfo(), body.userInfo)
    }
}
