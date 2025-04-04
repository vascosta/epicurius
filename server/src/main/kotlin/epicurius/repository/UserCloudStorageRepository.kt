package epicurius.repository

import org.springframework.web.multipart.MultipartFile

interface UserCloudStorageRepository {
    fun getProfilePicture(profilePictureName: String): ByteArray
    fun updateProfilePicture(profilePictureName: String, profilePicture: MultipartFile)
    fun deleteProfilePicture(profilePictureName: String)
}
