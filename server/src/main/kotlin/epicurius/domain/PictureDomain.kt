package epicurius.domain

import epicurius.domain.recipe.RecipeDomain.Companion.MAXIMUM_IMAGE_SIZE
import epicurius.domain.recipe.RecipeDomain.Companion.SUPPORTED_IMAGE_TYPES
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

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

    companion object {
        val SUPPORTED_IMAGE_TYPES = listOf("image/jpeg", "image/jpg", "image/png")
        const val MAXIMUM_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    }
}