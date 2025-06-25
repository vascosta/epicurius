package android.epicurius.ui.screens.feed

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
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
            val userFollowRequests = viewModel.userFollowRequests.collectAsState(idle())
            val collectionsToAddRecipeState = viewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = viewModel.collectionsToRemoveRecipe.collectAsState(idle())
            FeedScreen(
                userFeedState = userFeedState.value,
                followRequestsState = userFollowRequests.value,
                recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                    collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState.value
                ),
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
                    collectionsToRemove: List<CollectionProfile>
                    ->
                    viewModel.removeRecipeFromCollections(
                        recipeId,
                        collectionsToRemove
                    )
                },
                onCollectionsClear = { viewModel.clearRecipeCollections() },
                onAcceptFollowRequest = { name: String -> viewModel.acceptFollowRequest(name) },
                onRejectFollowRequest = { name: String -> viewModel.rejectFollowRequest(name) },
                onCollectionsRequest = { recipeId: Int ->
                    viewModel.getRecipeCollections(recipeId, CollectionType.FAVOURITE)
                },
                onFollowRequests = { viewModel.getUserFollowRequests() },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onUserProfileRequest = ::navigateToUserProfileActivity,
                onDailyMenuRequest = { navigateTo<DailyMenuActivity>(useStack = true) },
                onLoadMoreUserFeed = { viewModel.getUserFeed() },
                enableButtons = viewModel.enableButtons
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}
