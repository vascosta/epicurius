package epicurius.unit.repository.cloudStorage

import epicurius.domain.picture.PictureDomain
import epicurius.unit.repository.RepositoryTest
import org.springframework.web.multipart.MultipartFile
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CloudStorageRepositoryTest : RepositoryTest() {

    companion object {
        fun getPicture(profilePictureName: String, folder: String) = cs.pictureRepository.getPicture(profilePictureName, folder)

        fun updatePicture(profilePictureName: String, profilePicture: MultipartFile, folder: String) =
            cs.pictureRepository.updatePicture(profilePictureName, profilePicture, folder)

        fun deletePicture(profilePictureName: String, folder: String) =
            cs.pictureRepository.deletePicture(profilePictureName, folder)
    }

    @Test
    fun `Should add a picture to the Cloud Storage, retrieves it and then deletes it successfully`() {
        // given a picture
        val picture = testPicture
        val pictureName = randomUUID().toString()

        // when adding a picture
        updatePicture(pictureName, picture, PictureDomain.USERS_FOLDER)

        // then the picture is added successfully
        val newProfilePicture = getPicture(pictureName, PictureDomain.USERS_FOLDER)
        assertNotNull(newProfilePicture)
        assertContentEquals(picture.bytes, newProfilePicture)

        // when deleting the profile picture
        deletePicture(pictureName, PictureDomain.USERS_FOLDER)

        // then the profile picture is deleted successfully
        assertFailsWith<NullPointerException> { getPicture(pictureName, PictureDomain.USERS_FOLDER) }
    }
}
