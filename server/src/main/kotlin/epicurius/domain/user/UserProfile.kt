package epicurius.domain.user

data class UserProfile(
    val username: String,
    val country: String,
    val privacy: Boolean,
    val profilePicture: ByteArray?
)