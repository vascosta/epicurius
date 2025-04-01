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
    val diets: List<Diet>,
    val profilePictureName: String?
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            username = username,
            email = email,
            country = country,
            privacy = privacy,
            intolerances = intolerances,
            diets = diets,
            profilePictureName = profilePictureName
        )
    }
}
