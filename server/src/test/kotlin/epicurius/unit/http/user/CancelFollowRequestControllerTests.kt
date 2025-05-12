package epicurius.unit.http.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfCancelFollowRequest
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowRequestType
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CancelFollowRequestControllerTests : UserHttpTest() {

    @Test
    fun `Should cancel a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(authenticationRefreshHandlerMock.refreshToken(publicTestUser.token)).thenReturn(mockCookie)

        // when canceling the follow request
        val response = cancelFollowRequest(publicTestUser, privateTestUsername, mockResponse)

        // then the follow request is canceled successfully
        verify(userServiceMock).followRequest(publicTestUser.user.id, publicTestUsername, privateTestUsername, FollowRequestType.CANCEL)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw InvalidSelfCancelFollowRequest exception when canceling a follow request to himself`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.followRequest(publicTestUser.user.id, publicTestUsername, publicTestUsername, FollowRequestType.CANCEL))
            .thenThrow(InvalidSelfCancelFollowRequest())

        // when canceling the follow request
        // then the follow request is not canceled and throws InvalidSelfCancelFollowRequest exception
        assertFailsWith<InvalidSelfCancelFollowRequest> {
            cancelFollowRequest(publicTestUser, publicTestUsername, mockResponse)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when canceling a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.followRequest(publicTestUser.user.id, publicTestUsername, nonExistingUser, FollowRequestType.CANCEL))
            .thenThrow(UserNotFound(nonExistingUser))

        // when canceling the follow request
        // then the follow request is not canceled and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            cancelFollowRequest(publicTestUser, nonExistingUser, mockResponse)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when canceling a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(userServiceMock.followRequest(publicTestUser.user.id, publicTestUsername, privateTestUsername, FollowRequestType.CANCEL))
            .thenThrow(FollowRequestNotFound(privateTestUsername))

        // when canceling the follow request
        // then the follow request is not canceled and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            cancelFollowRequest(publicTestUser, privateTestUsername, mockResponse)
        }
    }
}
