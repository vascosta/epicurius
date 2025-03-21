package epicurius.domain.user

import epicurius.domain.user.User

data class AuthenticatedUser(val user: User, val token: String)