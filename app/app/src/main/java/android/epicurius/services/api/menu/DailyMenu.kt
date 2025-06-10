package android.epicurius.services.api.menu

import android.epicurius.domain.recipe.RecipeInfo
import java.time.LocalDate

data class DailyMenu (val date: String, val menu: Map<String, RecipeInfo?>)
