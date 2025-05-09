package epicurius.unit.http.user

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.user.models.output.UpdateUserProfilePictureOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateUserProfilePictureControllerTests : UserHttpTest() {

    @Test
    fun `Should add a profile picture to an user successfully`() {
        // given a user without a profile picture and a picture (testPicture)
        val authenticatedUser = AuthenticatedUser(publicTestUser.user.copy(profilePictureName = null), randomUUID().toString())

        // mock
        val mockPictureName = pictureDomain.generatePictureName()
        whenever(userServiceMock.updateProfilePicture(authenticatedUser.user.id, profilePicture = testPicture))
            .thenReturn(mockPictureName)
        whenever(authenticationRefreshHandlerMock.refreshToken(authenticatedUser.token)).thenReturn(mockCookie)

        // when adding a profile picture to the user
        val response = updateUserProfilePicture(authenticatedUser, testPicture, mockResponse)
        val body = response.body as UpdateUserProfilePictureOutputModel

        // then the profile picture is added successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockPictureName, body.profilePictureName)
    }

    @Test
    fun `Should update a profile picture of an user successfully`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(userServiceMock.updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName, testPicture))
            .thenReturn(publicTestUser.user.profilePictureName)
        whenever(authenticationRefreshHandlerMock.refreshToken(publicTestUser.token)).thenReturn(mockCookie)

        // when updating the profile picture of the user
        val response = updateUserProfilePicture(publicTestUser, testPicture, mockResponse)
        val body = response.body as UpdateUserProfilePictureOutputModel

        // then the profile picture is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUser.user.profilePictureName, body.profilePictureName)
    }

    @Test
    fun `Should remove the profile picture of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName))
            .thenReturn(null)
        whenever(authenticationRefreshHandlerMock.refreshToken(publicTestUser.token)).thenReturn(mockCookie)

        // when removing the profile picture of the user
        val response = updateUserProfilePicture(publicTestUser, null, mockResponse)

        // then the profile picture is removed successfully
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}
