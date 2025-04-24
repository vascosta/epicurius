package epicurius.domain.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class UserInfo(
    val name: String,
    val email: String,
    val country: String,
    val privacy: Boolean,
    val intolerances: Set<Intolerance>,
    val diets: Set<Diet>,
    val profilePictureName: String?
)
