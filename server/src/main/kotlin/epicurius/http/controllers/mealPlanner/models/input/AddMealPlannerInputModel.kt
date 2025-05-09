package epicurius.http.controllers.mealPlanner.models.input

import epicurius.domain.mealPlanner.MealTime

data class AddMealPlannerInputModel(val recipeId: Int, val mealTime: MealTime)
