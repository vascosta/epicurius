package android.epicurius.ui.screens.collections.favourites.folder

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.collections.list.FavouritesListActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouritesActivity : EpicuriusActivity() {
    override val viewModel: FavouritesViewModel by getViewModel<FavouritesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.collections.collectLatest { state ->
                if (state is Idle) viewModel.getCollections(null, CollectionType.FAVOURITE)
            }
        }
        setContent {
            val favouritesState = viewModel.collections.collectAsState(idle())
            FavouritesScreen(
                favouritesState = favouritesState.value,
                onBackButton = { navigateTo<SettingsActivity>(true) },
                onCollectionCreate = { collectionName: String ->
                    viewModel.createFavouriteCollection(collectionName) {
                        collectionId -> navigateToFavouritesListActivity(collectionId)
                    }
                },
                onCollectionRequest = ::navigateToFavouritesListActivity,
                onCollectionDelete = { collectionId: Int ->
                    viewModel.deleteCollection(collectionId)
                },
                onFavouritesRefresh = { viewModel.getCollections(null, CollectionType.FAVOURITE) },
                enableButtons = viewModel.enableButtons,
            )
        }
    }

    private fun navigateToFavouritesListActivity(collectionId: Int) {
        navigateTo<FavouritesListActivity> { intent ->
            intent.putExtra(Intents.COLLECTION_ID, collectionId)
        }
    }
}

