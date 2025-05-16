package android.epicurius.domain.user

data class UserProfile(
    val name: String,
    val country: String,
    val privacy: Boolean,
    val profilePicture: ByteArray?,
    val followersCount: Int,
    val followingCount: Int,
)
