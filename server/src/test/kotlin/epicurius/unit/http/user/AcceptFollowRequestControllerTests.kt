package epicurius.unit.http.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfAcceptFollowRequest
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowRequestType
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AcceptFollowRequestControllerTests : UserControllerTest() {

    @Test
    fun `Should accept a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when accepting the follow request
        val response = acceptFollowRequest(privateTestUser, publicTestUsername)

        // then the follow request is accepted successfully
        verify(userServiceMock).followRequest(privateTestUser.user.id, privateTestUsername, publicTestUsername, FollowRequestType.ACCEPT)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw InvalidSelfAcceptFollowRequest exception when accepting a follow request from himself`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.followRequest(publicTestUser.user.id, publicTestUsername, publicTestUsername, FollowRequestType.ACCEPT))
            .thenThrow(InvalidSelfAcceptFollowRequest())

        // when accepting the follow request
        // then the follow request is not accepted and throws InvalidSelfAcceptFollowRequest exception
        assertFailsWith<InvalidSelfAcceptFollowRequest> {
            acceptFollowRequest(publicTestUser, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when accepting a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.followRequest(privateTestUser.user.id, privateTestUsername, nonExistingUser, FollowRequestType.ACCEPT))
            .thenThrow(UserNotFound(nonExistingUser))

        // when accepting the follow request
        // then the follow request is not accepted and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            acceptFollowRequest(privateTestUser, nonExistingUser)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when accepting a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(userServiceMock.followRequest(privateTestUser.user.id, privateTestUsername, publicTestUsername, FollowRequestType.ACCEPT))
            .thenThrow(FollowRequestNotFound(privateTestUsername))

        // when accepting the follow request
        // then the follow request is not accepted and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            acceptFollowRequest(privateTestUser, publicTestUsername)
        }
    }
}
