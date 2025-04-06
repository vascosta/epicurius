package epicurius.repository.cloudStorage.picture

import org.springframework.web.multipart.MultipartFile

interface PictureRepository {
    fun getPicture(pictureName: String): ByteArray
    fun updatePicture(pictureName: String, picture: MultipartFile) // creates or updates
    fun deletePicture(pictureName: String)
}
