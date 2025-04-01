package epicurius.domain.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class UserInfo(
    val username: String,
    val email: String,
    val country: String,
    val privacy: Boolean,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val profilePictureName: String?
)
