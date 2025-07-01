package android.epicurius.domain.user

import java.util.Base64

data class UserProfile(
    val name: String,
    val country: String,
    val privacy: Boolean,
    val profilePicture: String?,
    val followersCount: Int,
    val followingCount: Int,
    val followingStatus: FollowingStatus,
) {
    val profilePictureBytes: ByteArray?
        get() = profilePicture?.let { Base64.getDecoder().decode(it) }
}
