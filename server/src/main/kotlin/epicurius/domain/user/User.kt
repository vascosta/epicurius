package epicurius.domain.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val tokenHash: String?,
    val country: String,
    val privacy: Boolean,
    val intolerances: List<Intolerance>,
    val diet: List<Diet>,
    val profilePictureName: String?
)
