package android.epicurius.domain.user

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance

data class UserInfo(
    val name: String,
    val email: String,
    val country: String,
    val privacy: Boolean,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val profilePictureName: String?
)
