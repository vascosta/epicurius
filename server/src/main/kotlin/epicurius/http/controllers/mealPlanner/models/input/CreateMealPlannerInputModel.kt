package epicurius.http.controllers.mealPlanner.models.input

import epicurius.domain.exceptions.InvalidMealPlannerDate
import java.time.LocalDate

data class CreateMealPlannerInputModel(
    val date: LocalDate,
    val maxCalories: Int? = null
) {
    init {
        if (date < LocalDate.now()) { throw InvalidMealPlannerDate() }
    }
}
