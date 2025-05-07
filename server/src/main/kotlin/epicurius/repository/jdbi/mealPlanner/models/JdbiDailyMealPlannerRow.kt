package epicurius.repository.jdbi.mealPlanner.models

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import java.time.LocalDate

data class JdbiDailyMealPlannerRow(
    val date: LocalDate,
    val maxCalories: Int?,
    val mealTime: MealTime,
    val jdbiRecipeInfo: JdbiRecipeInfo
)
