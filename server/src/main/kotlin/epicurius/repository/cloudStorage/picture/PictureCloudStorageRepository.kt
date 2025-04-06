package epicurius.repository.cloudStorage.picture

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.springframework.web.multipart.MultipartFile

class PictureCloudStorageRepository(private val cloudStorage: Storage) : PictureRepository {
    override fun getPicture(pictureName: String): ByteArray {
        val blob = getBlob(pictureName)
        return blob.getContent()
    }

    override fun updatePicture(pictureName: String, picture: MultipartFile) {
        val profilePictureBlobId = createBlobId(pictureName)

        val newProfilePicture = createBlobInfo(
            profilePictureBlobId,
            picture.contentType.toString() // already being checked before
        )
        cloudStorage.create(newProfilePicture, picture.bytes)
    }

    override fun deletePicture(pictureName: String) {
        val blob = getBlob(pictureName)

        if (blob.exists()) {
            blob.delete()
        }
    }

    private fun getBlob(objectName: String): Blob = cloudStorage.get(USERS_PROFILE_PICTURES_BUCKET, objectName)

    private fun createBlobId(objectName: String) = BlobId.of(USERS_PROFILE_PICTURES_BUCKET, objectName)

    private fun createBlobInfo(blobId: BlobId, contentType: String) =
        BlobInfo.newBuilder(blobId).setContentType(contentType).build()

    companion object {
        const val USERS_PROFILE_PICTURES_BUCKET = "epicurius_users_profile_pictures"
        const val RECIPES_PICTURES_BUCKET = "epicurius_recipes_pictures"
    }
}
