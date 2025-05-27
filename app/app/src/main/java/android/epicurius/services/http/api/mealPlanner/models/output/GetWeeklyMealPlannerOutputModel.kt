package android.epicurius.services.http.api.mealPlanner.models.output

import android.epicurius.domain.mealPlanner.DailyMealPlanner

data class GetWeeklyMealPlannerOutputModel(val planner: List<DailyMealPlanner>)
