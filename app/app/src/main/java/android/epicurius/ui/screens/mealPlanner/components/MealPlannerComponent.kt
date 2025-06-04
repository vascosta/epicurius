package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import androidx.compose.runtime.Composable

@Composable
fun MealPlannerComponent(dailyPlanner: DailyMealPlanner?) {
    MealTime.entries.forEach {
        val recipe = dailyPlanner?.meals[it]
        DailyMealPlannerBox(it, recipe)
    }
}
