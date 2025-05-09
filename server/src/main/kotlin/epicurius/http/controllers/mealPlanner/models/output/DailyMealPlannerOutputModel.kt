package epicurius.http.controllers.mealPlanner.models.output

import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.recipe.RecipeInfo
import java.time.LocalDate

data class DailyMealPlannerOutputModel (val daily: DailyMealPlanner)