package epicurius.http.controllers.mealPlanner.models.input

import jakarta.validation.constraints.Positive

data class UpdateDailyCaloriesInputModel(
    @Positive
    val maxCalories: Int? = null
)
