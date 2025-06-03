package android.epicurius.ui.screens.mealPlanner.weekly

import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class WeeklyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyScreen(
                onBackButton = { navigateTo<CalendarActivity>() },
                week = getWeek(LocalDate.now()),
                mealPlanner = MealPlanner(planner = emptyList())
            )
        }
    }
}