package epicurius.unit.http.user

import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.controllers.user.models.output.GetUserFollowRequestsOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFollowRequestsControllerTests : UserControllerTest() {

    @Test
    fun `Should retrieve the follow request of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = FollowUser(privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowRequests(publicTestUser.user.id)).thenReturn(mockFollowings)

        // when retrieving the follow requests of the user
        val response = getUserFollowRequests(publicTestUser)
        val body = response.body as GetUserFollowRequestsOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(mockFollowing.name, null), body.users.first())
    }
}
