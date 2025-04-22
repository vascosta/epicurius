package epicurius.unit.repository.cloudStorage

import epicurius.unit.repository.RepositoryTest
import org.springframework.web.multipart.MultipartFile

class CloudStorageRepositoryTests : RepositoryTest() {

    companion object {
        fun getPicture(profilePictureName: String, folder: String) = cs.pictureRepository.getPicture(profilePictureName, folder)

        fun updatePicture(profilePictureName: String, profilePicture: MultipartFile, folder: String) =
            cs.pictureRepository.updatePicture(profilePictureName, profilePicture, folder)

        fun deletePicture(profilePictureName: String, folder: String) =
            cs.pictureRepository.deletePicture(profilePictureName, folder)
    }

    /*@Test
    fun `Adds a profile picture to the Cloud Storage, retrieves it and then deletes it successfully`() {
        // given a profile picture
        val profilePicture = testPicture
        val profilePictureName = UUID.randomUUID().toString()

        // when adding a profile picture
        updateProfilePicture(profilePictureName, profilePicture)

        // then the profile picture is added successfully
        val newProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(profilePicture.bytes, newProfilePicture)

        // when deleting the profile picture
        deleteProfilePicture(profilePictureName)

        // then the profile picture is deleted successfully
        assertFailsWith<NullPointerException> { getProfilePicture(profilePictureName) }
    }

    @Test
    fun `Updates a profile picture already in the Cloud Storage and then retrieves it successfully`() {
        // given a profile picture in the Cloud Storage
        val profilePicture = testPicture
        val profilePictureName = UUID.randomUUID().toString()
        updateProfilePicture(profilePictureName, profilePicture)

        // when updating the profile picture
        val newProfilePicture = testPicture2
        updateProfilePicture(profilePictureName, newProfilePicture)

        // then the profile picture is updated successfully
        val updatedProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(newProfilePicture.bytes, updatedProfilePicture)
    }*/
}
