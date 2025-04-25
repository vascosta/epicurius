package epicurius.unit.http.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FollowControllerTests : UserHttpTest() {

    @Test
    fun `Should follow a public user successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when following a public user
        follow(privateTestUser, publicTestUsername)

        // then the user is followed successfully
        verify(userServiceMock).follow(privateTestUser.user.id, publicTestUsername)
    }

    @Test
    fun `Should get added to a private user follow requests list when following him successfully`() {
        // given two (publicTestUser and privateTestUser)

        // when following a private user
        follow(publicTestUser, privateTestUsername)

        // then a follow request is sent
        verify(userServiceMock).follow(publicTestUser.user.id, privateTestUsername)
    }

    @Test
    fun `Should throw UserNotFound exception when following a non-existing user`() {
        // given a user (publicTestUser) and a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.follow(publicTestUser.user.id, nonExistingUser)).thenThrow(UserNotFound(nonExistingUser))

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound exception
        assertFailsWith<UserNotFound> { follow(publicTestUser, nonExistingUser) }
    }

    @Test
    fun `Should throw UserAlreadyBeingFollowed exception when following a user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(userServiceMock.follow(privateTestUser.user.id, publicTestUsername)).thenThrow(UserAlreadyBeingFollowed(publicTestUsername))

        // when following a user twice
        // then the user cannot be followed again and throws UserAlreadyBeingFollowed exception
        assertFailsWith<UserAlreadyBeingFollowed> { follow(privateTestUser, publicTestUsername) }
    }

    @Test
    fun `Should throw FollowRequestAlreadyBeenSent when following a private user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(userServiceMock.follow(publicTestUser.user.id, privateTestUsername))
            .thenThrow(FollowRequestAlreadyBeenSent(publicTestUsername))

        // when trying to follow a private user twice
        // then another follow request cannot be sent again and throws FollowRequestAlreadyBeenSent exception
        assertFailsWith<FollowRequestAlreadyBeenSent> { follow(publicTestUser, privateTestUsername) }
    }
}
