package epicurius.repository

import epicurius.domain.Picture

interface UserCloudStorageRepository {
    fun getProfilePicture(profilePictureName: String): ByteArray
    fun updateProfilePicture(profilePictureName: String, profilePicture: Picture)
}