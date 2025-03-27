package epicurius.http.user.models.input

import org.springframework.web.multipart.MultipartFile

data class UpdateProfilePictureInputModel(val profilePicture: MultipartFile) {

    init {

        if (!SUPPORTED_IMAGE_TYPES.contains(profilePicture.contentType) || profilePicture.contentType == null) {
            throw IllegalArgumentException("Unsupported image type")
        }

        if (profilePicture.size > MAXIMUM_IMAGE_SIZE) {
            throw IllegalArgumentException("Image size too large")
        }

        if (profilePicture.isEmpty) {
            throw IllegalArgumentException("Image is empty")
        }
    }

    companion object {
        private val SUPPORTED_IMAGE_TYPES = listOf("image/jpeg", "image/jpg", "image/png")

        private const val MAXIMUM_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    }
}
