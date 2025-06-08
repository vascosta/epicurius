package android.epicurius.ui.screens.collections.favourites.folder

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.collections.favourites.list.FavouritesListActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouritesActivity : EpicuriusActivity() {
    override val viewModel: FavouritesViewModel by getViewModel<FavouritesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.favourites.collectLatest { state ->
                if (state is Idle) viewModel.getFavourites { navigateTo<SettingsActivity>() }
            }
        }
        setContent {
            val favouritesState = viewModel.favourites.collectAsState(idle())
            FavouritesScreen(
                favouritesState = favouritesState.value,
                onBackButton = { navigateTo<UserProfileActivity>() },
                onCollectionCreate = { name ->
                    viewModel.createFavouriteCollection(name) { id -> navigateToFavouritesListActivity(id) }
                },
                onCollectionRequest = ::navigateToFavouritesListActivity,
                onCollectionDelete = { id -> viewModel.deleteFavouriteCollection(id) },
                onFavouritesRefresh = { viewModel.getFavourites { navigateTo<SettingsActivity>() } },
                buttonsEnable = viewModel.buttonsEnable,
            )
        }
    }

    private fun navigateToFavouritesListActivity(collectionId: Int) {
        navigateTo<FavouritesListActivity> { intent ->
            intent.putExtra(Intents.COLLECTION_ID, collectionId)
        }
    }
}

