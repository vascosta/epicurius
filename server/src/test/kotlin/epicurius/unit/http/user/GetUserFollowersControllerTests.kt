package epicurius.unit.http.user

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
        val limit = 10

        // mock
        val mockFollower = FollowUser(1, privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, null, limit)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val response = getUserFollowers(publicTestUser, null, limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(1, mockFollower.name, null), body.users.first())
    }
}
