package epicurius.repository.cloudStorage

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import epicurius.repository.UserCloudStorageRepository

class UserCloudStorageRepository(private val cloudStorage: Storage): UserCloudStorageRepository {
    override fun getProfilePicture(profilePictureName: String): ByteArray {
        val blob = cloudStorage.get(USERS_PROFILE_PICTURES_BUCKET, profilePictureName)
        return blob.getContent()
    }

    override fun updateProfilePicture(username: String, profilePicture: ByteArray) {
        TODO("Not yet implemented")
    }

    private fun getBlobId(objectName: String, bucketName: String) = BlobId.of(bucketName, objectName)
    private fun getBlobInfo(objectName: String, bucketName: String) =
        BlobInfo.newBuilder(getBlobId(objectName, bucketName)).build()

    companion object {
        const val USERS_PROFILE_PICTURES_BUCKET = "epicurius_users_profile_pictures"
    }

}