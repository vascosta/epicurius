package android.epicurius.domain.mealPlanner

import android.epicurius.domain.recipe.RecipeInfo
import java.time.LocalDate

data class DailyMealPlanner(val date: LocalDate, val maxCalories: Int?, val meals: Map<MealTime, RecipeInfo>)
