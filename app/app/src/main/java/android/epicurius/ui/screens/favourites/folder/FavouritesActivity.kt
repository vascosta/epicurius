package android.epicurius.ui.screens.favourites.folder

import android.epicurius.MainActivity
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouritesActivity : EpicuriusActivity() {
    val viewModel: FavouritesViewModel by getViewModel<FavouritesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.favourites.collectLatest { state ->
                if (state is Idle) viewModel.getFavourites()
            }
        }
        setContent {
            val favouritesState = viewModel.favourites.collectAsState(idle())
            FavouritesScreen(
                onBackButton = { navigateTo<MainActivity>() },
                onFavouritesRefresh = { viewModel.refreshFavourites() },
                favouritesState = favouritesState.value,
            )
        }
    }
}