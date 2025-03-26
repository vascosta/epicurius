package epicurius.http.user.models.input

import org.springframework.web.multipart.MultipartFile

data class UpdateProfilePictureInputModel(val profilePicture: MultipartFile)
