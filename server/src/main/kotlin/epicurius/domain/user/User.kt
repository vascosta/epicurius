package epicurius.domain.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String,
    val tokenHash: String?,
    val country: String,
    val privacy: Boolean,
    val intolerances: Set<Intolerance>,
    val diets: Set<Diet>,
    val profilePictureName: String?
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            name = name,
            email = email,
            country = country,
            privacy = privacy,
            intolerances = intolerances,
            diets = diets,
            profilePictureName = profilePictureName
        )
    }
}
