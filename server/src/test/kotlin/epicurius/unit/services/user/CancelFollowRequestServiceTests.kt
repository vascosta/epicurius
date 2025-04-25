package epicurius.unit.services.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CancelFollowRequestServiceTests : UserServiceTest() {

    @Test
    fun `Should cancel a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(true)

        // when canceling the follow request
        cancelFollowRequest(publicTestUser.id, privateTestUsername)

        // then the follow request is canceled successfully
        verify(jdbiUserRepositoryMock).cancelFollowRequest(privateTestUser.id, publicTestUser.id)
    }

    @Test
    fun `Should throw UserNotFound exception when canceling a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUser)).thenReturn(null)

        // when canceling the follow request
        // then the follow request is not canceled and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            cancelFollowRequest(publicTestUser.id, nonExistingUser)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when canceling a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(false)

        // when canceling the follow request
        // then the follow request is not canceled and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            cancelFollowRequest(publicTestUser.id, privateTestUsername)
        }
    }
}
