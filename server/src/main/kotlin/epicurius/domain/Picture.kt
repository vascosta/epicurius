package epicurius.domain

import org.springframework.web.multipart.MultipartFile

data class Picture(val picture: MultipartFile) {

    val contentType = picture.contentType ?: throw IllegalArgumentException("Content type is null")

    init {

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
        private val SUPPORTED_IMAGE_TYPES = listOf("image/jpeg", "image/jpg", "image/png")

        private const val MAXIMUM_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    }
}
