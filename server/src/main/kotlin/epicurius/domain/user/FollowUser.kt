package epicurius.domain.user

data class FollowUser(
    val username: String,
    val profilePicture: ByteArray?
)

typealias FollowingUser = FollowUser
