package epicurius.unit.http.user

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.controllers.user.models.output.GetUserFollowersOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFollowersControllerTests : UserControllerTest() {

    @Test
    fun `Should retrieve the followers of an user successfully`() {
        // given a user (publicTestUser)
        val pagingParams = PagingParams()

        // mock
        val mockFollower = FollowUser(privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, pagingParams)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val response = getUserFollowers(publicTestUser, pagingParams.skip, pagingParams.limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(mockFollower.name, null), body.users.first())
    }
}
