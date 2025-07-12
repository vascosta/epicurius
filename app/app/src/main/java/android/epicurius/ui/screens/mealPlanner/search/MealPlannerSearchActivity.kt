package android.epicurius.ui.screens.mealPlanner.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.daily.DailyActivity
import android.epicurius.ui.screens.mealPlanner.weekly.WeeklyActivity
import android.epicurius.ui.screens.search.SearchRecipesViewModel
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealPlannerSearchActivity : EpicuriusActivity() {
    override val viewModel: SearchRecipesViewModel by getViewModel<SearchRecipesViewModel>()
    val mealPlannerSearchViewModel: MealPlannerSearchViewModel by getViewModel<MealPlannerSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userInfo.collectLatest { state ->
                if (state is Idle) viewModel.getUserInfo()
            }
        }
        setContent {
            val recipesResultState = viewModel.searchedRecipes.collectAsState(idle())
            val userInfoState = viewModel.userInfo.collectAsState(idle())
            MealPlannerSearchScreen(
                userInfoState = userInfoState.value,
                date = LocalDate.parse(intent.getStringExtra(Intents.DAILY_MEAL_PLANNER_DATE) ?: ""),
                mealTime = MealTime.valueOf(intent.getStringExtra(Intents.DAILY_MEAL_PLANNER_MEAL_TIME) ?: ""),
                recipesResultState = recipesResultState.value,
                onBackButton = { finish() },
                onSearchRecipes = {
                        name: String?,
                        cuisine: List<Cuisine>?,
                        mealType: List<MealType>?,
                        ingredients: Set<String>?,
                        intolerances: List<Intolerance>?,
                        diets: List<Diet>?,
                        servings: Int?,
                        minCalories: Int?,
                        maxCalories: Int?,
                        minCarbs: Int?,
                        maxCarbs: Int?,
                        minFat: Int?,
                        maxFat: Int?,
                        minProtein: Int?,
                        maxProtein: Int?,
                        minTime: Int?,
                        maxTime: Int?,
                        showAuthorRecipes: Boolean
                    ->
                    viewModel.searchRecipes(
                        name,
                        cuisine,
                        mealType,
                        ingredients,
                        intolerances,
                        diets,
                        servings,
                        minCalories,
                        maxCalories,
                        minCarbs,
                        maxCarbs,
                        minFat,
                        maxFat,
                        minProtein,
                        maxProtein,
                        minTime,
                        maxTime,
                        showAuthorRecipes
                    )
                },
                onAddRecipeToMealPlanner = { date: LocalDate, recipeId: Int, mealTime: MealTime ->
                    mealPlannerSearchViewModel.addRecipeToMealPlanner(date, recipeId, mealTime) { finish() }
                },
                onSearchRecipesClear = { viewModel.clearSearchRecipes() },
                enableButtons = viewModel.enableButtons
            )
        }
    }
}
