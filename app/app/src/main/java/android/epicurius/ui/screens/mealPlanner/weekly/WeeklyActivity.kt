package android.epicurius.ui.screens.mealPlanner.weekly

import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.mealPlanner.daily.DailyMealPlannerViewModel
import android.epicurius.ui.screens.mealPlanner.search.MealPlannerSearchActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class WeeklyActivity : EpicuriusActivity() {
    override val viewModel: WeeklyMealPlannerViewModel by getViewModel<WeeklyMealPlannerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.weeklyMealPlanner.collectLatest { state ->
                if (state is Idle) viewModel.getWeeklyMealPlanner { finish() }
            }
        }
        setContent {
            val weeklyMealPlannerState = viewModel.weeklyMealPlanner.collectAsState(idle())
            WeeklyScreen(
                week = getWeek(LocalDate.now()),
                weeklyMealPlannerState = weeklyMealPlannerState.value,
                onBackButton = { navigateTo<CalendarActivity>(finishCurrent = true) },
                onUpdateDailyCalories = { calories: Int ->
                    viewModel.updateDailyMealPlannerCalories(calories)
                },
                onDeleteRecipeFromMealPlanner = { date: LocalDate, mealtime: MealTime ->
                    viewModel.deleteRecipeFromDailyMealPlanner(date, mealtime)
                },
                onAddRecipeToMealPlannerRequest = ::navigateToMealPlannerSearchActivity,
                enableButtons = viewModel.enableButtons
            )
        }
    }

    private fun navigateToMealPlannerSearchActivity(date: LocalDate, mealTime: MealTime) {
        navigateTo<MealPlannerSearchActivity> { intent ->
            intent.putExtra(Intents.SOURCE_ACTIVITY, WeeklyActivity::class.java.name)
            intent.putExtra(Intents.DAILY_MEAL_PLANNER_DATE, date.toString())
            intent.putExtra(Intents.DAILY_MEAL_PLANNER_MEAl_TIME, mealTime.displayName)
        }
    }
}