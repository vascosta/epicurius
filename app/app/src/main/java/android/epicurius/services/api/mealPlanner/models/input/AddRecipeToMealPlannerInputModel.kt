package android.epicurius.services.api.mealPlanner.models.input

import android.epicurius.domain.mealPlanner.MealTime

data class AddRecipeToMealPlannerInputModel(val recipeId: Int, val mealTime: MealTime)
