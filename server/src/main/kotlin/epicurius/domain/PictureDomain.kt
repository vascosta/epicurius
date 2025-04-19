package epicurius.domain

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID.randomUUID

@Component
class PictureDomain {

    fun validatePicture(picture: MultipartFile) {
        if (!SUPPORTED_IMAGE_TYPES.contains(picture.contentType)) {
            throw IllegalArgumentException("Unsupported image type")
        }

        if (picture.size > MAXIMUM_IMAGE_SIZE) {
            throw IllegalArgumentException("Image size too large")
        }

        if (picture.isEmpty) {
            throw IllegalArgumentException("Image is empty")
        }
    }

    fun generatePictureName() = randomUUID().toString()

    companion object {
        val SUPPORTED_IMAGE_TYPES = listOf("image/jpeg", "image/jpg", "image/png")
        const val MAXIMUM_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB

        const val USERS_FOLDER = "users"
        const val RECIPES_FOLDER = "recipes"
        const val INGREDIENTS_FOLDER = "ingredients"
    }
}
