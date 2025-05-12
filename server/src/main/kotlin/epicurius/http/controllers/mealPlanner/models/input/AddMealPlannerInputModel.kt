package epicurius.http.controllers.mealPlanner.models.input

import epicurius.domain.mealPlanner.MealTime
import jakarta.validation.constraints.Positive

data class AddMealPlannerInputModel(
    @Positive
    val recipeId: Int,
    val mealTime: MealTime
)
