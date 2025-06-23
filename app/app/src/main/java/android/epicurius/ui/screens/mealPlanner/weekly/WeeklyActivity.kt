package android.epicurius.ui.screens.mealPlanner.weekly

import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.search.MealPlannerSearchActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class WeeklyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyScreen(
                week = getWeek(LocalDate.now()),
                mealPlanner = MealPlanner(planner = emptyList()),
                onBackButton = { navigateTo<CalendarActivity>() },
                onCaloriesUpdate = {  },
                onAddRecipe = { navigateTo<MealPlannerSearchActivity>() },
                onDeleteRecipe = { _, _ -> }
            )
        }
    }
}