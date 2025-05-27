package android.epicurius.services.http.api.mealPlanner.models.input

import android.epicurius.domain.mealPlanner.MealTime

data class UpdateMealPlannerInputModel(val recipeId: Int, val mealTime: MealTime)
