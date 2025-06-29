package epicurius.unit.http.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.controllers.user.models.output.GetUserFollowingOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserFollowingControllerTests : UserControllerTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the following of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = FollowUser(1, privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, null, null, null, limit)).thenReturn(mockFollowings)

        // when retrieving the following of the user
        val response = getUserFollowing(publicTestUser, null, null, null, limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), body.users.first())
    }

    @Test
    fun `Should search the following of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = FollowUser(1, privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, null, privateTestUsername, null, limit)).thenReturn(mockFollowings)

        // when searching the following of the user
        val response = getUserFollowing(publicTestUser, null, privateTestUsername, null, limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), body.users.first())
    }

    @Test
    fun `Should retrieve the following of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = FollowUser(1, privateTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, privateTestUsername, null, null, limit)).thenReturn(mockFollowings)

        // when retrieving the following of a user
        val response = getUserFollowing(publicTestUser, privateTestUsername, null, null, limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), body.users.first())
    }

    @Test
    fun `Should search the following of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = FollowUser(1, publicTestUsername, null)
        val mockFollowings = listOf(mockFollowing)
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, privateTestUsername, publicTestUsername, null, limit)).thenReturn(mockFollowings)

        // when searching the following of a user
        val response = getUserFollowing(publicTestUser, privateTestUsername, publicTestUsername, null, limit)
        val body = response.body as GetUserFollowingOutputModel

        // then the following are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowings.size, body.users.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), body.users.first())
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving or searching the following from non-existing user`() {
        // given a non-existing user
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, nonExistingUsername, null, null, limit))
            .thenThrow(UserNotFound(nonExistingUsername))

        // when retrieving or searching the following
        // then the user the following cannot be retrieved or searched and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            getUserFollowing(publicTestUser, nonExistingUsername, null, null, limit)
        }
    }
}
