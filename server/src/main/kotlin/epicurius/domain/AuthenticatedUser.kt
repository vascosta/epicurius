package epicurius.domain

import User

data class AuthenticatedUser(val user: User, val token: String)