package epicurius.domain.user

data class AuthenticatedUser(val user: User, val token: String)
