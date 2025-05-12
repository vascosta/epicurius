package epicurius.http.controllers.mealPlanner.models.input

import epicurius.domain.exceptions.InvalidMealPlannerDate
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class CreateMealPlannerInputModel(
    val date: LocalDate,

    @Positive
    val maxCalories: Int? = null
) {
    init {
        if (date < LocalDate.now()) { throw InvalidMealPlannerDate() }
    }
}
