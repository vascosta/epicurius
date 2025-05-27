package android.epicurius.services.http.api.mealPlanner.models.input

import java.time.LocalDate

data class CreateMealPlannerInputModel(val date: LocalDate, val maxCalories: Int? = null)
