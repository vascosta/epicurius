package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import androidx.compose.runtime.Composable
import java.time.LocalDate

@Composable
fun MealPlannerComponent(
    dailyPlanner: DailyMealPlanner?,
    date: LocalDate,
    onDeleteRecipeFromMealPlanner: (date: LocalDate, mealtime: MealTime) -> Unit = { _, _, -> },
    onAddRecipeToMealPlannerRequest: (date: LocalDate, mealTime: MealTime) -> Unit = {},
    enableButtons: Boolean
) {
    MealTime.entries.forEach {
        val recipe = dailyPlanner?.meals[it]
        DailyMealPlannerBox(
            mealTime = it,
            recipe = recipe,
            dailyMealPlannerDate = date,
            onAddRecipeToMealPlannerRequest = onAddRecipeToMealPlannerRequest,
            onDeleteRecipeFromMealPlanner = onDeleteRecipeFromMealPlanner,
            enableButtons = enableButtons
        )
    }
}
