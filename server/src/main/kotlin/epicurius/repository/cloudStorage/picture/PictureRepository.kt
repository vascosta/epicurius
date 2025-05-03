package epicurius.repository.cloudStorage.picture

import epicurius.config.CloudStorage
import epicurius.repository.cloudStorage.picture.contract.PictureRepository
import org.springframework.web.multipart.MultipartFile

class PictureRepository(private val cloudStorage: CloudStorage) : PictureRepository {

    override fun getPicture(pictureName: String, folder: String): ByteArray {
        val blob = cloudStorage.getBlob("$folder/$pictureName")
        return blob.getContent()
    }

    override fun updatePicture(pictureName: String, picture: MultipartFile, folder: String) {
        val profilePictureBlobId = cloudStorage.createBlobId("$folder/$pictureName")

        val newProfilePicture = cloudStorage.createBlobInfo(
            profilePictureBlobId,
            picture.contentType.toString() // already being checked before
        )
        cloudStorage.storage.create(newProfilePicture, picture.bytes)
    }

    override fun deletePicture(pictureName: String, folder: String) {
        val blob = cloudStorage.getBlob("$folder/$pictureName")

        if (blob.exists()) {
            blob.delete()
        }
    }
}
