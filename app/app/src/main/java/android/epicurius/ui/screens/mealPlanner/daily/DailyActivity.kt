package android.epicurius.ui.screens.mealPlanner.daily

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.search.MealPlannerSearchActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class DailyActivity : EpicuriusActivity() {
    override val viewModel: DailyMealPlannerViewModel by getViewModel<DailyMealPlannerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyScreen(
                dailyMealPlannerState = ,
                date = LocalDate.now(),
                onBackButton = { navigateTo<CalendarActivity>() },
                onCaloriesUpdate = {  },
                onDeleteRecipe = { _, _ -> },
                onAddRecipeToMealPlannerRequest = { navigateTo<MealPlannerSearchActivity>() },
                enableButtons = viewModel.enableButtons
            )
        }
    }

    private fun navigateToMealPlannerSearchActivity(mealTime: MealTime) {
        navigateTo<MealPlannerSearchActivity> {
            intent.putExtra(Intents.SOURCE_ACTIVITY, DailyActivity::class.java.name)
            intent.putExtra(Intents.MEAL_TIME, mealTime.displayName)
        }
    }
}
