package android.epicurius.ui.screens.mealPlanner.daily

import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.mealPlanner.search.MealPlannerSearchActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.epicurius.ui.screens.utils.Idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyActivity : EpicuriusActivity() {
    override val viewModel: DailyMealPlannerViewModel by getViewModel<DailyMealPlannerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.dailyMealPlanner.collectLatest { state ->
                if (state is Idle)
                    viewModel.getDailyMealPlanner(
                        LocalDate.parse(intent.getStringExtra(Intents.DAILY_MEAL_PLANNER_DATE))
                    )
            }
        }
        setContent {
            val dailyMealPlannerState = viewModel.dailyMealPlanner.collectAsState()
            DailyScreen(
                dailyMealPlannerState = dailyMealPlannerState.value,
                date = LocalDate.parse(intent.getStringExtra(Intents.DAILY_MEAL_PLANNER_DATE)),
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

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch { viewModel.resetDailyMealPlanner() }
    }

    private fun navigateToMealPlannerSearchActivity(date: LocalDate, mealTime: MealTime) {
        navigateTo<MealPlannerSearchActivity> { intent ->
            intent.putExtra(Intents.DAILY_MEAL_PLANNER_DATE, date.toString())
            intent.putExtra(Intents.DAILY_MEAL_PLANNER_MEAL_TIME, mealTime.name)
        }
    }
}
