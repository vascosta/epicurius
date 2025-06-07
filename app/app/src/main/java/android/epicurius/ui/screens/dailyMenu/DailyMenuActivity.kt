package android.epicurius.ui.screens.dailyMenu

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DailyMenuActivity : EpicuriusActivity() {
    val viewModel: DailyMenuViewModel by getViewModel<DailyMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.dailyMenu.collectLatest { state ->
                if (state is Idle) viewModel.getDailyMenu()
            }
        }
        setContent {
            val menuState = viewModel.dailyMenu.collectAsState(idle())
            DailyMenuScreen(
                menuState = menuState.value,
                onBackButton = { navigateTo<FeedActivity>() },
                onAddRecipeToCollection = TODO(),
                onRemoveRecipeFromCollection = TODO(),
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onDailyMenuRefresh = { viewModel.getDailyMenu() },
                enableButtons = TODO()
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}