package epicurius.unit.http.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.InvalidSelfFollow
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.unit.http.recipe.RecipeHttpTest.Companion.testAuthenticatedUser
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FollowControllerTests : UserHttpTest() {

    @Test
    fun `Should follow a public user successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(authenticationRefreshHandlerMock.refreshToken(privateTestUser.token)).thenReturn(mockCookie)

        // when following a public user
        val response = follow(privateTestUser, publicTestUsername,  mockResponse)

        // then the user is followed successfully
        verify(userServiceMock).follow(privateTestUser.user.id, privateTestUsername, publicTestUsername)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should get added to a private user follow requests list when following him successfully`() {
        // given two (publicTestUser and privateTestUser)

        // mock
        whenever(authenticationRefreshHandlerMock.refreshToken(publicTestUser.token)).thenReturn(mockCookie)

        // when following a private user
        val response = follow(publicTestUser, privateTestUsername, mockResponse)

        // then a follow request is sent
        verify(userServiceMock).follow(publicTestUser.user.id, publicTestUsername, privateTestUsername)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw InvalidSelfFollow exception when following yourself`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.follow(publicTestUser.user.id, publicTestUsername, publicTestUsername))
            .thenThrow(InvalidSelfFollow())

        // when following himself
        // then the user cannot follow himself and throws InvalidSelfFollow exception
        assertFailsWith<InvalidSelfFollow> {
            follow(publicTestUser, publicTestUsername, mockResponse)
        }
    }

    @Test
    fun `Should throw UserNotFound exception when following a non-existing user`() {
        // given a user (publicTestUser) and a non-existing user
        val nonExistingUser = "nonExistingUser"

        // mock
        whenever(userServiceMock.follow(publicTestUser.user.id, publicTestUsername, nonExistingUser)).thenThrow(UserNotFound(nonExistingUser))

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound exception
        assertFailsWith<UserNotFound> { follow(publicTestUser, nonExistingUser, mockResponse) }
    }

    @Test
    fun `Should throw UserAlreadyBeingFollowed exception when following a user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(userServiceMock.follow(privateTestUser.user.id, privateTestUsername, publicTestUsername)).thenThrow(UserAlreadyBeingFollowed(publicTestUsername))

        // when following a user twice
        // then the user cannot be followed again and throws UserAlreadyBeingFollowed exception
        assertFailsWith<UserAlreadyBeingFollowed> { follow(privateTestUser, publicTestUsername, mockResponse) }
    }

    @Test
    fun `Should throw FollowRequestAlreadyBeenSent when following a private user twice`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        whenever(userServiceMock.follow(publicTestUser.user.id, publicTestUsername, privateTestUsername))
            .thenThrow(FollowRequestAlreadyBeenSent(publicTestUsername))

        // when trying to follow a private user twice
        // then another follow request cannot be sent again and throws FollowRequestAlreadyBeenSent exception
        assertFailsWith<FollowRequestAlreadyBeenSent> { follow(publicTestUser, privateTestUsername, mockResponse) }
    }
}
