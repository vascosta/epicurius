package epicurius.unit.http.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfRejectFollowRequest
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowRequestType
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RejectFollowRequestControllerTests: UserControllerTest() {

    @Test
    fun `Should reject a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when rejecting the follow request
        val response = rejectFollowRequest(privateTestUser, publicTestUsername)

        // then the follow request is rejected successfully
        verify(userServiceMock).followRequest(privateTestUser.user.id, privateTestUsername, publicTestUsername, FollowRequestType.REJECT)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw InvalidSelfRejectFollowRequest exception when rejecting a follow request from himself`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.followRequest(publicTestUser.user.id, publicTestUsername, publicTestUsername, FollowRequestType.REJECT))
            .thenThrow(InvalidSelfRejectFollowRequest())

        // when rejecting the follow request
        // then the follow request is not rejected and throws InvalidSelfRejectFollowRequest exception
        assertFailsWith<InvalidSelfRejectFollowRequest> {
            rejectFollowRequest(publicTestUser, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when rejecting a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.followRequest(privateTestUser.user.id, privateTestUsername, nonExistingUser, FollowRequestType.REJECT))
            .thenThrow(UserNotFound(nonExistingUser))

        // when rejecting the follow request
        // then the follow request is not rejected and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            rejectFollowRequest(privateTestUser, nonExistingUser)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when rejecting a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(userServiceMock.followRequest(privateTestUser.user.id, privateTestUsername, publicTestUsername, FollowRequestType.REJECT))
            .thenThrow(FollowRequestNotFound(privateTestUsername))

        // when rejecting the follow request
        // then the follow request is not rejected and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            rejectFollowRequest(privateTestUser, publicTestUsername)
        }
    }
}