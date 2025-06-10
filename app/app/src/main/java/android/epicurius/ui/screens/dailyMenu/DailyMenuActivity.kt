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
                if (state is Idle) viewModel.getDailyMenu { navigateTo<FeedActivity>(true) }
            }
        }
        setContent {
            val menuState = viewModel.dailyMenu.collectAsState(idle())
            val collectionsState = viewModel.collections.collectAsState(idle())
            DailyMenuScreen(
                menuState = menuState.value,
                collectionsState = collectionsState.value,
                onBackButton = { navigateTo<FeedActivity>(true) },
                onAddRecipeToCollection = { collectionId: Int, recipeId: Int ->
                    viewModel.addRecipeToCollection(collectionId, recipeId)
                },
                onRemoveRecipeFromCollection = { collectionId: Int, recipeId: Int ->
                    viewModel.removeRecipeFromCollection(collectionId, recipeId)
                },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onCollectionsRequest = { recipeId: Int, recipeInCollection: Boolean ->
                    viewModel.getCollections(
                        recipeId,
                        recipeInCollection,
                        viewModel.collectionsFlow,
                        viewModel.cachedCollectionsFlow
                    )
                },
                onDailyMenuRefresh = { viewModel.getDailyMenu { navigateTo<FeedActivity>(true) } },
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