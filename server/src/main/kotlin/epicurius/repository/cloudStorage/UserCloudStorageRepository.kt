package epicurius.repository.cloudStorage

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import epicurius.repository.UserCloudStorageRepository
import org.springframework.web.multipart.MultipartFile

class UserCloudStorageRepository(private val cloudStorage: Storage) : UserCloudStorageRepository {
    override fun getProfilePicture(profilePictureName: String): ByteArray {
        val blob = getBlob(profilePictureName)
        return blob.getContent()
    }

    override fun updateProfilePicture(profilePictureName: String, profilePicture: MultipartFile) {
        val profilePictureBlobId = createBlobId(profilePictureName)

        val newProfilePicture = createBlobInfo(
            profilePictureBlobId,
            profilePicture.contentType.toString() // already being checked in the input model
        )
        cloudStorage.create(newProfilePicture, profilePicture.bytes)
    }

    override fun deleteProfilePicture(profilePictureName: String) {
        val blob = getBlob(profilePictureName)

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
    }
}
