package epicurius.repository.jdbi.mealPlanner.models

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import java.util.Date

data class JdbiDailyMealPlanner(val date: Date, val meals: Map<MealTime, JdbiRecipeInfo>)
