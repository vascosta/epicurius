package epicurius.unit.services.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.SearchUser
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetFollowersServiceTests : UserServiceTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the followers of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getFollowers(publicTestUser.id, null, null, limit)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val followers = getFollowers(publicTestUser.id, null, null, null, limit)

        // then the followers are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should search the followers of the user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getFollowers(publicTestUser.id, privateTestUsername, null, limit)).thenReturn(mockFollowers)

        // when searching the followers of the user
        val followers = getFollowers(publicTestUser.id, null, privateTestUsername, null, limit)

        // then the followers are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should retrieve the followers of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.getFollowers(privateTestUser.id, null, null, limit)).thenReturn(mockFollowers)

        // when retrieving the followers of a user
        val followers = getFollowers(publicTestUser.id, privateTestUsername, null, null, limit)

        // then the followers are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should search the followers of another user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollower = SearchUserModel(1, publicTestUsername, publicTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.getFollowers(privateTestUser.id, publicTestUsername, null, limit)).thenReturn(mockFollowers)

        // when searching the followers of a user
        val followers = getFollowers(publicTestUser.id, privateTestUsername, publicTestUsername, null, limit)

        // then the followers are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(1, mockFollower.name, null), followers.first())
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving or searching the followers from non-existing user`() {
        // given a non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUsername)).thenReturn(null)

        // when retrieving or searching the followers
        // then the user the followers cannot be retrieved or searched and throws UserNotFound exception
        assertFailsWith<UserNotFound> { getFollowers(publicTestUser.id, nonExistingUsername, null, null, limit) }
    }
}
