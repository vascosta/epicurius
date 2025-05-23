package epicurius.unit.http.user

import epicurius.domain.PagingParams
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
        val pagingParams = PagingParams()

        // mock
        val mockFollowing = FollowUser(privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, pagingParams)).thenReturn(mockFollowings)

        // when retrieving the following of the user
        val response = getUserFollowing(publicTestUser, pagingParams.skip, pagingParams.limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(mockFollowing.name, null), body.users.first())
    }
}
