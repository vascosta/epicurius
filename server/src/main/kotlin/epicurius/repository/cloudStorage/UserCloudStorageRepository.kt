package epicurius.repository.cloudStorage

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import epicurius.domain.Picture
import epicurius.domain.exceptions.ProfilePictureNotFound
import epicurius.repository.UserCloudStorageRepository

class UserCloudStorageRepository(private val cloudStorage: Storage): UserCloudStorageRepository {
    override fun getProfilePicture(profilePictureName: String): ByteArray {
        val blob = getBlob(profilePictureName, USERS_PROFILE_PICTURES_BUCKET)
        return blob.getContent()
    }

    override fun updateProfilePicture(profilePictureName: String, profilePicture: Picture) {
        val profilePictureBlobId = createBlobId(profilePictureName, USERS_PROFILE_PICTURES_BUCKET)

        val newProfilePicture = createBlobInfo(
            profilePictureBlobId,
            profilePicture.contentType
        )
        cloudStorage.create(newProfilePicture, profilePicture.picture.bytes)
    }

    private fun getBlob(objectName: String, bucketName: String): Blob = cloudStorage.get(bucketName, objectName)

    private fun createBlobId(objectName: String, bucketName: String) = BlobId.of(bucketName, objectName)

    private fun createBlobInfo(blobId: BlobId, contentType: String) =
        BlobInfo.newBuilder(blobId).setContentType(contentType).build()

    companion object {
        const val USERS_PROFILE_PICTURES_BUCKET = "epicurius_users_profile_pictures"
    }

}