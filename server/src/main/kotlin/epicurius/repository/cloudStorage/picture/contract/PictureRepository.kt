package epicurius.repository.cloudStorage.picture.contract

import org.springframework.web.multipart.MultipartFile

interface PictureRepository {
    fun getPicture(pictureName: String, folder: String): ByteArray
    fun updatePicture(pictureName: String, picture: MultipartFile, folder: String) // creates or updates
    fun deletePicture(pictureName: String, folder: String)
}
