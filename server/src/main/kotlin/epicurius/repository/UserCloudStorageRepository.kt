package epicurius.repository

interface UserCloudStorageRepository {
    fun getProfilePicture(username: String): ByteArray
    fun updateProfilePicture(username: String, profilePicture: ByteArray)
}