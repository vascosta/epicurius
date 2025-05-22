package epicurius.unit.http.user

import epicurius.domain.exceptions.PictureNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.user.models.output.UpdateUserProfilePictureOutputModel
import epicurius.unit.services.ServiceTest.Companion.updateProfilePicture
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserProfilePictureControllerTests : UserControllerTest() {

    @Test
    fun `Should add a profile picture to an user successfully`() {
        // given a user without a profile picture and a picture (testPicture)
        val authenticatedUser = AuthenticatedUser(publicTestUser.user.copy(profilePictureName = null), randomUUID().toString())

        // mock
        val mockPictureName = pictureDomain.generatePictureName()
        whenever(userServiceMock.updateProfilePicture(authenticatedUser.user.id, profilePicture = testPicture))
            .thenReturn(mockPictureName)

        // when adding a profile picture to the user
        val response = updateUserProfilePicture(authenticatedUser, testPicture)
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

        // when updating the profile picture of the user
        val response = updateUserProfilePicture(publicTestUser, testPicture)
        val body = response.body as UpdateUserProfilePictureOutputModel

        // then the profile picture is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUser.user.profilePictureName, body.profilePictureName)
    }

    @Test
    fun `Should throw PictureNotFound exception when updating a profile picture of an user with a non-existing profile picture name`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(userServiceMock.updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName, testPicture))
            .thenThrow(PictureNotFound())

        // when updating the profile picture of the user
        // then the user profile picture cannot be updated and throws PictureNotFound exception
        assertFailsWith<PictureNotFound> {
            updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName, testPicture)
        }
    }

    @Test
    fun `Should remove the profile picture of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName))
            .thenReturn(null)

        // when removing the profile picture of the user
        val response = updateUserProfilePicture(publicTestUser, null)

        // then the profile picture is removed successfully
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw PictureNotFound exception when removing a profile picture of an user with a non-existing profile picture name`() {
        // given a user (publicTestUser)

        // mock
        whenever(userServiceMock.updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName, null))
            .thenThrow(PictureNotFound())

        // when removing the profile picture of the user
        // then the user profile picture cannot be removed and throws PictureNotFound exception
        assertFailsWith<PictureNotFound> {
            updateProfilePicture(publicTestUser.user.id, publicTestUser.user.profilePictureName, null)
        }
    }
}
