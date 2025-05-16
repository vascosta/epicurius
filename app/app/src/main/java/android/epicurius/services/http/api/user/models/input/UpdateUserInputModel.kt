package android.epicurius.services.http.api.user.models.input

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance

data class UpdateUserInputModel(
    val name: String? = null,
    val email: String? = null,
    val country: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val privacy: Boolean? = null,
    val intolerances: Set<Intolerance>? = null,
    val diets: Set<Diet>? = null
)
