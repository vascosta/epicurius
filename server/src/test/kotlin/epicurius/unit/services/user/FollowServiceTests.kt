package epicurius.unit.services.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.InvalidSelfFollow
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowingStatus
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FollowServiceTests : UserServiceTest() {

    @Test
    fun `Should follow a public user successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(publicTestUser.id, privateTestUser.id))
            .thenReturn(false)

        // when following a public user
        follow(privateTestUser.id, privateTestUsername, publicTestUsername)

        // then the user is followed successfully
        verify(jdbiUserRepositoryMock).follow(privateTestUser.id, publicTestUser.id, FollowingStatus.ACCEPTED.ordinal)
    }

    @Test
    fun `Should get added to a private user follow requests list when following him successfully`() {
        // given two (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(privateTestUser.id, publicTestUser.id))
            .thenReturn(false)

        // when following a private user
        follow(publicTestUser.id, publicTestUsername, privateTestUsername)

        // then a follow request is sent
        verify(jdbiUserRepositoryMock).follow(publicTestUser.id, privateTestUser.id, FollowingStatus.PENDING.ordinal)
    }

    @Test
    fun `Should throw InvalidSelfFollow exception when following yourself`() {
        // given a user (publicTestUser)

        // when following himself
        // then the user cannot follow himself and throws InvalidSelfFollow exception
        assertFailsWith<InvalidSelfFollow> {
            follow(publicTestUser.id, publicTestUsername, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when following a non-existing user`() {
        // given a user (publicTestUser) and a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUser)).thenReturn(null)

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound exception
        assertFailsWith<UserNotFound> { follow(publicTestUser.id, publicTestUsername, nonExistingUser) }
    }

    @Test
    fun `Should throw UserAlreadyBeingFollowed exception when following a user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(publicTestUser.id, privateTestUser.id))
            .thenReturn(true)

        // when following a user twice
        // then the user cannot be followed again and throws UserAlreadyBeingFollowed exception
        assertFailsWith<UserAlreadyBeingFollowed> { follow(privateTestUser.id, privateTestUsername, publicTestUsername) }
    }

    @Test
    fun `Should throw FollowRequestAlreadyBeenSent when following a private user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(privateTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsBeingFollowedBy(privateTestUser.id, publicTestUser.id))
            .thenReturn(false)
        whenever(jdbiUserRepositoryMock.checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id))
            .thenReturn(true)

        // when trying to follow a private user twice
        // then another follow request cannot be sent again and throws FollowRequestAlreadyBeenSent exception
        assertFailsWith<FollowRequestAlreadyBeenSent> { follow(publicTestUser.id, publicTestUsername, privateTestUsername) }
    }
}
