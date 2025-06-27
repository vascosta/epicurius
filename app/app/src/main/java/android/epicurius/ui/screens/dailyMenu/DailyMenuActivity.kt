package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DailyMenuActivity : EpicuriusActivity() {
    override val viewModel: DailyMenuViewModel by getViewModel<DailyMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.dailyMenu.collectLatest { state ->
                if (state is Idle) viewModel.getDailyMenu()
            }
        }
        setContent {
            val menuState = viewModel.dailyMenu.collectAsState(idle())
            val collectionsToAddRecipeState = viewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = viewModel.collectionsToRemoveRecipe.collectAsState(idle())
            DailyMenuScreen(
                menuState = menuState.value,
                recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                    collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState.value
                ),
                onBackButton = { navigateTo<FeedActivity>(useStack = true) },
                onAddRecipeToCollections = {
                    recipeId: Int,
                    collectionsToAdd: List<CollectionProfile>
                    ->
                    viewModel.addRecipeToCollections(
                        recipeId,
                        collectionsToAdd
                    )
                },
                onRemoveRecipeFromCollections = {
                    recipeId: Int,
                    collectionsToRemove: List<CollectionProfile>,
                     ->
                    viewModel.removeRecipeFromCollections(
                        recipeId,
                        collectionsToRemove
                    )
                },
                onClearRecipeCollections = { viewModel.clearRecipeCollections() },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onRecipeCollectionsRequest = { recipeId: Int ->
                    viewModel.getRecipeCollections(recipeId, CollectionType.FAVOURITE)
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