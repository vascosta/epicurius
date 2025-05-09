package epicurius.http.controllers.mealPlanner.models.input

import epicurius.domain.mealPlanner.MealTime
import jakarta.validation.constraints.Positive

data class UpdateMealPlannerInputModel(
    @Positive
    val recipeId: Int,
    val mealTime: MealTime
)
