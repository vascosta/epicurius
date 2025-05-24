package epicurius.unit.http.user

import epicurius.domain.exceptions.InvalidSelfUnfollow
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UnfollowControllerTests : UserControllerTest() {

    @Test
    fun `Should unfollow a user successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when unfollowing a user
        val response = unfollow(publicTestUser, privateTestUsername)

        // then the user is unfollowed successfully
        verify(userServiceMock).unfollow(publicTestUser.user.id, publicTestUsername, privateTestUsername)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw InvalidSelfUnfollow exception when unfollowing yourself`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.unfollow(publicTestUser.user.id, publicTestUsername, publicTestUsername))
            .thenThrow(InvalidSelfUnfollow())

        // when unfollowing himself
        // then the user cannot be unfollowed and throws InvalidSelfUnfollow exception
        assertFailsWith<InvalidSelfUnfollow> {
            unfollow(publicTestUser, publicTestUsername)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when unfollowing a non-existing user`() {
        // given a user (publicTestUser) and a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.unfollow(publicTestUser.user.id, publicTestUsername, nonExistingUser)).thenThrow(UserNotFound(nonExistingUser))

        // when following a non-existing user
        // then the user cannot be unfollowed and throws UserNotFound exception
        assertFailsWith<UserNotFound> { unfollow(publicTestUser, nonExistingUser) }
    }

    @Test
    fun `Should throw UserNotFollowed when unfollowing a user that is not being followed`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(userServiceMock.unfollow(publicTestUser.user.id, publicTestUsername, privateTestUsername)).thenThrow(UserNotFollowed(privateTestUsername))

        // when trying to unfollow a user that is not being followed
        // then the user cannot be unfollowed and throws UserNotFollowed exception
        assertFailsWith<UserNotFollowed> { unfollow(publicTestUser, privateTestUsername) }
    }
}
