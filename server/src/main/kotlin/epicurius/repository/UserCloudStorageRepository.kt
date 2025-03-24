package epicurius.repository

interface UserCloudStorageRepository {
    fun getProfilePicture(profilePictureName: String): ByteArray
    fun updateProfilePicture(username: String, profilePicture: ByteArray)
}