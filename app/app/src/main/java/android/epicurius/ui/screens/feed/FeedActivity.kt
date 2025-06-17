package android.epicurius.ui.screens.feed

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.list.components.CollectionsStateBundle
import android.epicurius.ui.screens.dailyMenu.DailyMenuActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedActivity : EpicuriusActivity() {
    override val viewModel: FeedViewModel by getViewModel<FeedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userFeed.collectLatest { state ->
                if (state is Idle) viewModel.getUserFeed()
            }
        }
        setContent {
            val userFeedState = viewModel.userFeed.collectAsState(idle())
            val collectionsToAddRecipeState = viewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = viewModel.collectionsToRemoveRecipe.collectAsState(idle())
            FeedScreen(
                userFeedState = userFeedState.value,
                collectionsStateBundle = CollectionsStateBundle(
                    collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState.value
                ),
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
                onCollectionsRequest = { recipeId: Int -> viewModel.getCollections(recipeId,
                    CollectionType.FAVOURITE) },
                onDailyMenuRequest = { navigateTo<DailyMenuActivity>(true) },
                onCollectionsClear = { viewModel.clearCollections() },
                onUserFeedRefresh = { viewModel.refreshUserFeed() },
                onFollowRequests = { listOf(SearchUser(1, "Test User", null)) },
                onAcceptFollowRequest = {  },
                onRejectFollowRequest = {  },
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
