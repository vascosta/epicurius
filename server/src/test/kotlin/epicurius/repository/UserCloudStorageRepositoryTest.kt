package epicurius.repository

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

class UserCloudStorageRepositoryTest : RepositoryTest() {

    @Test
    fun `Adds a profile picture to the Cloud Storage and then retrieves it successfully`() {
        // given a profile picture
        val profilePicture = testProfilePicture
        val profilePictureName = UUID.randomUUID().toString()

        // when adding a profile picture
        updateProfilePicture(profilePictureName, profilePicture)

        // then the profile picture is added successfully
        val newProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(profilePicture.bytes, newProfilePicture)
    }

    @Test
    fun `Updates a profile picture already in the Cloud Storage and then retrieves it successfully`() {
        // given a profile picture in the Cloud Storage
        val profilePicture = testProfilePicture
        val profilePictureName = UUID.randomUUID().toString()
        updateProfilePicture(profilePictureName, profilePicture)

        // when updating the profile picture
        val newProfilePicture = testProfilePicture2
        updateProfilePicture(profilePictureName, newProfilePicture)

        // then the profile picture is updated successfully
        val updatedProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(newProfilePicture.bytes, updatedProfilePicture)
    }
}
