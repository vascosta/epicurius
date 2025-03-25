package epicurius.domain.user

data class SearchUser(
    val username: String,
    val profilePicture: ByteArray?
)

typealias FollowUser = SearchUser

typealias FollowingUser = SearchUser