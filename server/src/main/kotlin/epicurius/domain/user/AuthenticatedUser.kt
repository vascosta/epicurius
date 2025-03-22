package epicurius.domain.user

data class AuthenticatedUser(val userInfo: User, val token: String)