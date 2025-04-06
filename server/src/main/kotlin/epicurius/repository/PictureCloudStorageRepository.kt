package epicurius.repository

import org.springframework.web.multipart.MultipartFile

interface PictureCloudStorageRepository {
    fun getPicture(pictureName: String): ByteArray
    fun updatePicture(pictureName: String, picture: MultipartFile) // creates or updates
    fun deletePicture(pictureName: String)
}
