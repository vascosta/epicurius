package android.epicurius.ui.screens.mealPlanner.calendar

import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.search.MealPlannerSearchActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class CalendarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarScreen(
                onWeeklyMealPlannerRequest = { navigateTo<WeeklyActivity>() },
                onDailyMealPlannerRequest = ::navigateToDailyMealPlannerActivity
            )
        }
    }

    private fun navigateToDailyMealPlannerActivity(date: LocalDate) {
        navigateTo<MealPlannerSearchActivity> {
            intent.putExtra(Intents.SOURCE_ACTIVITY, WeeklyActivity::class.java.name)
            intent.putExtra(Intents.DAILY_MEAL_PLANNER_DATE, date.toString())
        }
    }
}