package epicurius.unit.services.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.SearchUser
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetFollowingServiceTests : UserServiceTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the following of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowings = listOf(mockFollowing)
        whenever(jdbiUserRepositoryMock.getFollowing(publicTestUser.id, null, null, limit)).thenReturn(mockFollowings)

        // when retrieving the following of the user
        val followings = getFollowing(publicTestUser.id, null, null, null, limit)

        // then the following are retrieved successfully
        assertEquals(mockFollowings.size, followings.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), followings.first())
    }

    @Test
    fun `Should search the following of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowings = listOf(mockFollowing)
        whenever(jdbiUserRepositoryMock.getFollowing(publicTestUser.id, privateTestUsername, null, limit)).thenReturn(mockFollowings)

        // when searching the following of the user
        val followings = getFollowing(publicTestUser.id, null, privateTestUsername, null, limit)

        // then the following are retrieved successfully
        assertEquals(mockFollowings.size, followings.size)
        assertEquals(SearchUser(1, mockFollowing.name, null), followings.first())
    }

    @Test
    fun `Should retrieve the following of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.getFollowing(privateTestUser.id, null, null, limit)).thenReturn(mockFollowers)

        // when retrieving the following of a user
        val followers = getFollowing(publicTestUser.id, privateTestUsername, null, null, limit)

        // then the following are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should search the following of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, publicTestUsername, publicTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.getFollowing(privateTestUser.id, publicTestUsername, null, limit)).thenReturn(mockFollowers)

        // when retrieving the following of a user
        val followers = getFollowing(publicTestUser.id, privateTestUsername, publicTestUsername, null, limit)

        // then the following are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving or searching the following from non-existing user`() {
        // given a non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUsername)).thenReturn(null)

        // when retrieving or searching the following
        // then the user the following cannot be retrieved or searched and throws UserNotFound exception
        assertFailsWith<UserNotFound> { getFollowing(publicTestUser.id, nonExistingUsername, null, null, limit) }
    }
}
