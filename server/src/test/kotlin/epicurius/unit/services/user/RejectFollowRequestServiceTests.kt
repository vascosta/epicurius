package epicurius.unit.services.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfAcceptFollowRequest
import epicurius.domain.exceptions.InvalidSelfRejectFollowRequest
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RejectFollowRequestServiceTests : UserServiceTest() {

    @Test
    fun `Should reject a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(true)

        // when rejecting the follow request
        rejectFollowRequest(privateTestUser.id, privateTestUsername, publicTestUsername)

        // then the follow request is rejected successfully
        verify(jdbiUserRepositoryMock).rejectFollowRequest(privateTestUser.id, publicTestUser.id)
    }

    @Test
    fun `Should throw InvalidSelfRejectFollowRequest exception when rejecting a follow request to himself`() {
        // given a user (publicTestUser)

        // when rejecting the follow request
        // then the follow request is not rejected and throws InvalidSelfRejectFollowRequest exception
        assertFailsWith<InvalidSelfRejectFollowRequest> {
            rejectFollowRequest(publicTestUser.id, publicTestUsername, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when rejecting a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUser)).thenReturn(null)

        // when rejecting the follow request
        // then the follow request is not rejected and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            rejectFollowRequest(privateTestUser.id, privateTestUsername, nonExistingUser)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when rejecting a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(false)

        // when rejecting the follow request
        // then the follow request is not rejected and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            rejectFollowRequest(privateTestUser.id, privateTestUsername, publicTestUsername)
        }
    }
}