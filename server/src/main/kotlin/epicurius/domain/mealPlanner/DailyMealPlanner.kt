package epicurius.domain.mealPlanner

import epicurius.domain.recipe.RecipeInfo
import java.util.Date

data class DailyMealPlanner(val date: Date, val meals: Map<MealTime, RecipeInfo>)
