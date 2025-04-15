package epicurius.http.mealPlanner.models.output

import epicurius.domain.mealPlanner.DailyMealPlanner

data class GetMealPlannerOutputModel(val planner: List<DailyMealPlanner>)
