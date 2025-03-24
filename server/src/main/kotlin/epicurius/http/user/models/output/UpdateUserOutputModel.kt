package epicurius.http.user.models.output

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class UpdateUserOutputModel (
    val username: String,
    val email: String,
    val country: String,
    val privacy: Boolean,
    val intolerances: List<Intolerance>,
    val diet: List<Diet>,
    val profilePicture: String?
)