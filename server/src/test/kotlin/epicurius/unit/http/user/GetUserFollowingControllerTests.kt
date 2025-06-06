package epicurius.unit.http.user

import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.controllers.user.models.output.GetUserFollowingOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFollowingControllerTests : UserControllerTest() {

    @Test
    fun `Should retrieve the following of an user successfully`() {
        // given a user (publicTestUser)
        val limit = 10

        // mock
        val mockFollowing = FollowUser(1, privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, null, limit)).thenReturn(mockFollowings)

        // when retrieving the following of the user
        val response = getUserFollowing(publicTestUser, null, limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), body.users.first())
    }
}
