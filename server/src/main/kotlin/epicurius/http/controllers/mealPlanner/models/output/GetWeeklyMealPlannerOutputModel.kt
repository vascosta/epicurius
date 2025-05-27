package epicurius.http.controllers.mealPlanner.models.output

import epicurius.domain.mealPlanner.DailyMealPlanner

data class GetWeeklyMealPlannerOutputModel(val planner: List<DailyMealPlanner>)
