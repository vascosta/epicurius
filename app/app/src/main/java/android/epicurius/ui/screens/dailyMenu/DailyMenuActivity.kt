package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.components.CollectionsStateBundle
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
            val collectionsToAddRecipeState = viewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = viewModel.collectionsToRemoveRecipe.collectAsState(idle())
            DailyMenuScreen(
                menuState = menuState.value,
                collectionsStateBundle = CollectionsStateBundle(
                    collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState.value
                ),
                onBackButton = { navigateTo<FeedActivity>(true) },
                onAddRecipeToCollections = {
                    collectionsAvailableToAdd: List<CollectionProfile>,
                    collectionsAvailableToRemove: List<CollectionProfile>,
                    collectionsToAdd: List<CollectionProfile>,
                    recipeId: Int ->
                    viewModel.addRecipeToCollections(
                        collectionsAvailableToAdd,
                        collectionsAvailableToRemove,
                        collectionsToAdd,
                        recipeId
                    )
                },
                onRemoveRecipeFromCollections = {
                    collectionsAvailableToAdd: List<CollectionProfile>,
                    collectionsAvailableToRemove: List<CollectionProfile>,
                    collectionsToRemove: List<CollectionProfile>,
                    recipeId: Int ->
                    viewModel.removeRecipeFromCollections(
                        collectionsAvailableToAdd,
                        collectionsAvailableToRemove,
                        collectionsToRemove,
                        recipeId
                    )
                },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onCollectionsRequest = { recipeId: Int -> viewModel.getCollections(recipeId) },
                onCollectionsClear = { viewModel.clearCollections() },
                onDailyMenuRefresh = {
                    viewModel.refreshDailyMenu { navigateTo<FeedActivity>(true) }
                },
                enableButtons = viewModel.enableButtons
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}