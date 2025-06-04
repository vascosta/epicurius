package android.epicurius.ui.screens.favourites.list

import android.epicurius.MainActivity
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.Intents
import android.epicurius.ui.screens.favourites.folder.FavouritesActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FavouritesListActivity : EpicuriusActivity() {
    val viewModel: FavouritesListViewModel by getViewModel<FavouritesListViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            combine(
                viewModel.recipes,
                viewModel.favouritesListName
            ) { recipesState, favouritesNameState -> recipesState to favouritesNameState }
                .collectLatest { (recipesState, favouritesNameState) ->
                    val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1)
                    if (recipesState is Idle || favouritesNameState is Idle) {
                        viewModel.getCollection(recipeId) { navigateTo<FavouritesActivity>() }
                    }
                }
        }
        setContent {
            val recipes = viewModel.recipes.collectAsState(idle())
            val favouritesListName = viewModel.favouritesListName.collectAsState(idle())
            FavouritesListScreen(
                onBackButton = { navigateTo<MainActivity>() },
                onFavouritesRefresh = {
                    val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1)
                    viewModel.getCollection(recipeId) { navigateTo<FavouritesActivity>() }
                },
                favouritesListNameState = favouritesListName.value,
                recipesState = recipes.value
            )
        }
    }
}