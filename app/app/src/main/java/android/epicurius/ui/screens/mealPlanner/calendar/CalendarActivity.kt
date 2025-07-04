package android.epicurius.ui.screens.mealPlanner.calendar

import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.daily.DailyActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CalendarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarScreen(
                onWeeklyMealPlannerRequest = { navigateTo<WeeklyActivity>() },
                onDailyMealPlannerRequest = { navigateTo<DailyActivity>() }
            )
        }
    }
}