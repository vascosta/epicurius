package epicurius.unit.http.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.SearchUser
import epicurius.http.controllers.user.models.output.GetUserFollowersOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class GetUserFollowersControllerTests : UserControllerTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the followers of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = FollowUser(1, privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, null, null, null, limit)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val response = getUserFollowers(publicTestUser, null, null, null, limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(1, mockFollower.name, null), body.users.first())
    }

    @Test
    fun `Should search the followers of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = FollowUser(1, privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, null, privateTestUsername, null, limit)).thenReturn(mockFollowers)

        // when searching the followers of the user
        val response = getUserFollowers(publicTestUser, null, privateTestUsername, null, limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(1, mockFollower.name, null), body.users.first())
    }

    @Test
    fun `Should retrieve the followers of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = FollowUser(1, privateTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, privateTestUsername, null, null, limit)).thenReturn(mockFollowers)

        // when retrieving the followers of a user
        val response = getUserFollowers(publicTestUser, privateTestUsername, null, null, limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(1, mockFollower.name, null), body.users.first())
    }

    @Test
    fun `Should search the followers of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = FollowUser(1, publicTestUsername, null)
        val mockFollowers = listOf(mockFollower)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, privateTestUsername, publicTestUsername, null, limit)).thenReturn(mockFollowers)

        // when searching the followers of a user
        val response = getUserFollowers(publicTestUser, privateTestUsername, publicTestUsername, null, limit)
        val body = response.body as GetUserFollowersOutputModel

        // then the followers are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockFollowers.size, body.users.size)
        assertEquals(SearchUser(1, mockFollower.name, null), body.users.first())
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving or searching the followers from non-existing user`() {
        // given a non-existing user
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, nonExistingUsername, null, null, limit))
            .thenThrow(UserNotFound(nonExistingUsername))

        // when retrieving or searching the followers
        // then the user the followers cannot be retrieved or searched and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            getUserFollowers(publicTestUser, nonExistingUsername, null, null, limit)
        }
    }
}
