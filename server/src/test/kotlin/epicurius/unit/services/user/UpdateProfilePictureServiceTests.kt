package epicurius.unit.services.user

import epicurius.domain.exceptions.PictureNotFound
import epicurius.domain.picture.PictureDomain
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateProfilePictureServiceTests : UserServiceTest() {

    @Test
    fun `Should add a profile picture to an user successfully`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        val mockPictureName = pictureDomain.generatePictureName()
        whenever(pictureDomainMock.generatePictureName()).thenReturn(mockPictureName)
        whenever(jdbiUserRepositoryMock.updateUser(publicTestUser.id, JdbiUpdateUserModel(profilePictureName = mockPictureName)))
            .thenReturn(publicTestUser.copy(profilePictureName = mockPictureName))

        // when adding a profile picture to the user
        val profilePictureName = updateProfilePicture(publicTestUser.id, profilePicture = testPicture)

        // then the profile picture is added successfully
        verify(pictureDomainMock).validatePicture(testPicture)
        verify(pictureRepositoryMock).updatePicture(mockPictureName, testPicture, PictureDomain.USERS_FOLDER)
        assertNotNull(profilePictureName)
        assertEquals(mockPictureName, profilePictureName)
    }

    @Test
    fun `Should update a profile picture of an user successfully`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(jdbiUserRepositoryMock.getUserProfilePictureName(publicTestUser.id)).thenReturn(publicTestUser.profilePictureName)

        // when updating the profile picture of the user
        val profilePictureName = updateProfilePicture(publicTestUser.id, publicTestUser.profilePictureName, testPicture)

        // then the profile picture is updated successfully
        verify(pictureDomainMock).validatePicture(testPicture)
        verify(pictureRepositoryMock).updatePicture(publicTestUser.profilePictureName!!, testPicture, PictureDomain.USERS_FOLDER)
        assertNotNull(profilePictureName)
        assertEquals(publicTestUser.profilePictureName, profilePictureName)
    }

    @Test
    fun `Should throw PictureNotFound exception when updating a profile picture of an user with a non-existing profile picture name`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(jdbiUserRepositoryMock.getUserProfilePictureName(publicTestUser.id)).thenReturn(null)

        // when updating the profile picture of the user
        // then the user profile picture cannot be updated and throws PictureNotFound exception
        assertFailsWith<PictureNotFound> {
            updateProfilePicture(publicTestUser.id, publicTestUser.profilePictureName, testPicture)
        }
    }

    @Test
    fun `Should remove the profile picture of an user successfully`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(jdbiUserRepositoryMock.getUserProfilePictureName(publicTestUser.id)).thenReturn(publicTestUser.profilePictureName)
        whenever(jdbiUserRepositoryMock.updateUser(publicTestUser.id, JdbiUpdateUserModel(profilePictureName = null)))
            .thenReturn(publicTestUser.copy(profilePictureName = null))

        // when removing the profile picture of the user
        val profilePictureName = updateProfilePicture(publicTestUser.id, publicTestUser.profilePictureName, null)

        // then the profile picture is removed successfully
        verify(pictureRepositoryMock).deletePicture(publicTestUser.profilePictureName!!, PictureDomain.USERS_FOLDER)
        assertNull(profilePictureName)
    }

    @Test
    fun `Should throw PictureNotFound exception when removing a profile picture of an user with a non-existing profile picture name`() {
        // given a user and a picture (publicTestUser, testPicture)

        // mock
        whenever(jdbiUserRepositoryMock.getUserProfilePictureName(publicTestUser.id)).thenReturn(null)

        // when removing the profile picture of the user
        // then the user profile picture cannot be removed and throws PictureNotFound exception
        assertFailsWith<PictureNotFound> {
            updateProfilePicture(publicTestUser.id, publicTestUser.profilePictureName, null)
        }
    }
}
