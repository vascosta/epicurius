package android.epicurius.services.api.mealPlanner.models.output

import android.epicurius.domain.mealPlanner.DailyMealPlanner

data class GetWeeklyMealPlannerOutputModel(val planner: List<DailyMealPlanner>)
