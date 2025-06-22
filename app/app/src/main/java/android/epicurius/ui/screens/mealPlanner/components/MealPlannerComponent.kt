package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import androidx.compose.runtime.Composable
import java.time.LocalDate

@Composable
fun MealPlannerComponent(
    dailyPlanner: DailyMealPlanner?,
    date: LocalDate,
    onAddRecipe: () -> Unit,
    onDeleteRecipe: (LocalDate, MealTime) -> Unit
) {
    MealTime.entries.forEach {
        val recipe = dailyPlanner?.meals[it]
        DailyMealPlannerBox(
            mealTime = it,
            recipe = recipe,
            dailyMealPlannerDate = date,
            onAddRecipe = onAddRecipe,
            onDeleteRecipe = onDeleteRecipe
        )
    }
}
