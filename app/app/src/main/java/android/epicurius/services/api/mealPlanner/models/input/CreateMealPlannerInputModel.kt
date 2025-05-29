package android.epicurius.services.api.mealPlanner.models.input

import java.time.LocalDate

data class CreateMealPlannerInputModel(val date: LocalDate, val maxCalories: Int? = null)
