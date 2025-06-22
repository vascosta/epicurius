package android.epicurius.ui.screens.search.general

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.search.camera.CameraActivity
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState

class SearchActivity : EpicuriusActivity() {
    override val viewModel: SearchViewModel by getViewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val usersResultState = viewModel.searchedUsers.collectAsState(idle())
            SearchScreen(
                usersResultState = usersResultState.value,
                onBackButton = { finish() },
                onRecipeSearch = { navigateTo<RecipeProfileActivity>() },
                onSearchUsers = { name: String -> viewModel.searchUsers(name) },
                onSearchUsersClear = { viewModel.clearSearchUsers() },
                onCamera = { navigateTo<CameraActivity>() },
                onLoadMoreSearchedUsers = { name: String -> viewModel.searchUsers(name) },
                onUserProfileRequest = ::navigateToUserProfileActivity,
                enableButtons = viewModel.enableButtons,
            )
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}