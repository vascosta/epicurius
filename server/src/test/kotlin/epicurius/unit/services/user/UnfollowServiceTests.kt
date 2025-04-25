package epicurius.unit.services.user

import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UnfollowServiceTests : UserServiceTest() {

    @Test
    fun `Should unfollow a user successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(publicTestUser.id, privateTestUser.id))
            .thenReturn(true)

        // when unfollowing a user
        unfollow(privateTestUser.id, publicTestUsername)

        // then the user is unfollowed successfully
        verify(jdbiUserRepositoryMock).unfollowUser(privateTestUser.id, publicTestUser.id)
    }

    @Test
    fun `Should throw UserNotFound exception when unfollowing a non-existing user`() {
        // given a user (publicTestUser) and a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUser)).thenReturn(null)

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound exception
        assertFailsWith<UserNotFound> { unfollow(publicTestUser.id, nonExistingUser) }
    }

    @Test
    fun `Should throw UserNotFollowed when unfollowing a user that is not being followed`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(publicTestUser.id, privateTestUser.id))
            .thenReturn(false)

        // when trying to unfollow a user that is not being followed
        // then the user cannot be unfollowed and throws UserNotFollowed exception
        assertFailsWith<UserNotFollowed> { unfollow(privateTestUser.id, publicTestUsername) }
    }
}
