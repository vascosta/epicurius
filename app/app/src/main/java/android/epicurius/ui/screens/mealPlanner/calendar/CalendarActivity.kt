package android.epicurius.ui.screens.mealPlanner.calendar

import android.epicurius.ui.screens.mealPlanner.daily.DailyActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarScreen(
                onWeeklyPlanner = { navigateTo<WeeklyActivity>() },
                onDayClick = { selectedDate ->
                    navigateTo<DailyActivity>()
                }
            )
        }
    }
}