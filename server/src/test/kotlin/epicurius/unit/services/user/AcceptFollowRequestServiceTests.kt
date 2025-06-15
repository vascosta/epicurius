package epicurius.unit.services.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfAcceptFollowRequest
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AcceptFollowRequestServiceTests : UserServiceTest() {

    @Test
    fun `Should accept a follow request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(true)

        // when accepting the follow request
        acceptFollowRequest(privateTestUser.id, privateTestUsername, publicTestUsername)

        // then the follow request is accepted successfully
        verify(jdbiUserRepositoryMock).acceptFollowRequest(privateTestUser.id, publicTestUser.id)
    }

    @Test
    fun `Should throw InvalidSelfAcceptFollowRequest exception when accepting a follow request to himself`() {
        // given a user (publicTestUser)

        // when accepting the follow request
        // then the follow request is not accepted and throws InvalidSelfAcceptFollowRequest exception
        assertFailsWith<InvalidSelfAcceptFollowRequest> {
            acceptFollowRequest(publicTestUser.id, publicTestUsername, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when accepting a follow request for a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUser)).thenReturn(null)

        // when accepting the follow request
        // then the follow request is not accepted and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            acceptFollowRequest(privateTestUser.id, privateTestUsername, nonExistingUser)
        }
    }

    @Test
    fun `Should throw FollowRequestNotFound exception when accepting a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(false)

        // when accepting the follow request
        // then the follow request is not accepted and throws FollowRequestNotFound exception
        assertFailsWith<FollowRequestNotFound> {
            acceptFollowRequest(privateTestUser.id, privateTestUsername, publicTestUsername)
        }
    }
}