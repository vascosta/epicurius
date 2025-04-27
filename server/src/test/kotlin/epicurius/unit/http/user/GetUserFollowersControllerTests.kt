package epicurius.unit.http.user

import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.user.models.output.GetUserFollowersOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserFollowersControllerTests : UserHttpTest() {

    @Test
    fun `Should retrieve the followers of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = FollowUser(privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val response = getUserFollowers(publicTestUser)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(mockFollower.name, null), body.users.first())
    }
}
