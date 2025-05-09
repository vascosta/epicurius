package epicurius.http.controllers.mealPlanner.models.output

import epicurius.domain.mealPlanner.DailyMealPlanner

data class MealPlannerOutputModel(val planner: List<DailyMealPlanner>)
