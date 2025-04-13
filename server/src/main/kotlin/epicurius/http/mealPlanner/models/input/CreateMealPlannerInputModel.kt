package epicurius.http.mealPlanner.models.input

import epicurius.domain.exceptions.InvalidMealPlannerDate
import java.time.Instant
import java.util.Date

data class CreateMealPlannerInputModel(
    val date: Date
) {
    init {
        if (date < Date.from(Instant.now())) { throw InvalidMealPlannerDate() }
    }
}
