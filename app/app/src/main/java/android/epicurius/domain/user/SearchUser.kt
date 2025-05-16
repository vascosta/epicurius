package android.epicurius.domain.user

data class SearchUser(val name: String, val profilePicture: ByteArray?)

typealias FollowUser = SearchUser

typealias FollowingUser = SearchUser
