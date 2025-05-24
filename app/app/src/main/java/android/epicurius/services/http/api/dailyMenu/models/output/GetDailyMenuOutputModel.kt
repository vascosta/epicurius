package android.epicurius.services.http.api.dailyMenu.models.output

import android.epicurius.domain.recipe.RecipeInfo

data class GetDailyMenuOutputModel(val menu: Map<String, RecipeInfo?>)
