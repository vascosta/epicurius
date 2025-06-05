package android.epicurius.ui.screens.feed

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
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

class FeedActivity : EpicuriusActivity() {
    val viewModel: FeedViewModel by getViewModel<FeedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userFeed.collectLatest { state ->
                if (state is Idle) viewModel.getUserFeed()
            }
        }
        setContent {
            val userFeedState = viewModel.userFeed.collectAsState(idle())
            FeedScreen(
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onUserFeedRefresh = { viewModel.refreshUserFeed() },
                userFeedState = userFeedState.value
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}
