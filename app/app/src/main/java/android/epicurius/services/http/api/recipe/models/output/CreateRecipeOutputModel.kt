package android.epicurius.services.http.api.recipe.models.output

import android.epicurius.domain.recipe.Recipe

data class CreateRecipeOutputModel(val recipe: Recipe)

typealias GetRecipeOutputModel = CreateRecipeOutputModel
