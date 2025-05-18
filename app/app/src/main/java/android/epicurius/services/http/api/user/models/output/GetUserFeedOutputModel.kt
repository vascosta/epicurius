package android.epicurius.services.http.api.user.models.output

import android.epicurius.domain.recipe.RecipeInfo

data class GetUserFeedOutputModel(val feed: List<RecipeInfo>)
