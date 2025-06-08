package android.epicurius.ui.screens.dailyMenu

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DailyMenuActivity : EpicuriusActivity() {
    override val viewModel: DailyMenuViewModel by getViewModel<DailyMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.dailyMenu.collectLatest { state ->
                if (state is Idle) viewModel.getDailyMenu { navigateTo<FeedActivity>() }
            }
        }
        setContent {
            val menuState = viewModel.dailyMenu.collectAsState(idle())
            val collectionsState = viewModel.collections.collectAsState(idle())
            DailyMenuScreen(
                menuState = menuState.value,
                collectionsState = collectionsState.value,
                onBackButton = { navigateTo<FeedActivity>() },
                onAddRecipeToCollection = { collectionId, recipeId ->
                    viewModel.addRecipeToCollection(collectionId, recipeId) { navigateTo<FeedActivity>() }
                },
                onRemoveRecipeFromCollection = { collectionId, recipeId ->
                    viewModel.removeRecipeFromCollection(collectionId, recipeId) { navigateTo<FeedActivity>() }
                },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onCollectionsRequest = { recipeId, recipeInCollection ->
                    viewModel.getCollections(recipeId, recipeInCollection) },
                onDailyMenuRefresh = { viewModel.getDailyMenu { navigateTo<FeedActivity>() } },
                buttonsEnable = viewModel.buttonsEnable
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}