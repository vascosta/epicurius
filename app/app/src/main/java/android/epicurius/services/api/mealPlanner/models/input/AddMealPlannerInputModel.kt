package android.epicurius.services.api.mealPlanner.models.input

import android.epicurius.domain.mealPlanner.MealTime

data class AddMealPlannerInputModel(val recipeId: Int, val mealTime: MealTime)
