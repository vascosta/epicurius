package android.epicurius.ui.screens.collections.favourites

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.collections.collection.CollectionActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouritesActivity : EpicuriusActivity() {
    override val viewModel: CollectionsViewModel by getViewModel<CollectionsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.collections.collectLatest { state ->
                if (state is Idle) viewModel.getCollections(CollectionType.FAVOURITE)
            }
        }
        setContent {
            val favouritesState = viewModel.collections.collectAsState(idle())
            FavouritesScreen(
                favouritesState = favouritesState.value,
                onBackButton = { navigateTo<SettingsActivity>(useStack = true) },
                onCollectionCreate = { collectionName: String ->
                    viewModel.createCollection(collectionName, CollectionType.FAVOURITE) { collectionId ->
                        navigateToFavouritesListActivity(collectionId, true)
                    }
                },
                onCollectionDelete = { collectionId: Int ->
                    viewModel.deleteCollection(collectionId)
                },
                onCollectionRequest = ::navigateToFavouritesListActivity,
                onLoadMoreFavourites = { viewModel.getCollections(CollectionType.FAVOURITE) },
                enableButtons = viewModel.enableButtons,
            )
        }
    }

    private fun navigateToFavouritesListActivity(collectionId: Int, isCollectionOwner: Boolean) {
        navigateTo<CollectionActivity> { intent ->
            intent.putExtra(Intents.SOURCE_ACTIVITY, FavouritesActivity::class.java.name)
            intent.putExtra(Intents.COLLECTION_ID, collectionId)
            intent.putExtra(Intents.IS_COLLECTION_OWNER, isCollectionOwner)
        }
    }
}