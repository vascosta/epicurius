package android.epicurius.ui.screens.collections.favourites.list

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.collections.favourites.folder.FavouritesActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FavouritesListActivity : EpicuriusActivity() {
    override val viewModel: FavouritesListViewModel by getViewModel<FavouritesListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            combine(
                viewModel.recipes,
                viewModel.favouritesListName
            ) { recipesState, favouritesNameState -> recipesState to favouritesNameState }
                .collectLatest { (recipesState, favouritesNameState) ->
                    val collectionId = intent.getIntExtra(Intents.COLLECTION_ID, -1)
                    if (recipesState is Idle || favouritesNameState is Idle) {
                        viewModel.getFavouriteCollection(collectionId) { navigateTo<FavouritesActivity>() }
                    }
                }
        }
        setContent {
            val recipes = viewModel.recipes.collectAsState(idle())
            val favouritesListName = viewModel.favouritesListName.collectAsState(idle())
            FavouritesListScreen(
                collectionId = intent.getIntExtra(Intents.COLLECTION_ID, -1),
                favouritesListNameState = favouritesListName.value,
                recipesState = recipes.value,
                onBackButton = { navigateTo<FavouritesActivity>() },
                onCollectionEdit = { collectionId: Int, collectionName: String ->
                    viewModel.updateFavouriteCollection(collectionId, collectionName) { navigateTo<FavouritesActivity>() }
                },
                onCollectionDelete = { collectionId: Int ->
                    viewModel.deleteFavouriteCollection(collectionId) { navigateTo<FavouritesActivity>() }
                },
                onRecipeDelete = { collectionId: Int, recipeId: Int ->
                    viewModel.removeRecipeFromFavouriteCollection(collectionId, recipeId)
                },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                onFavouriteCollectionRefresh = {
                    val collectionId = intent.getIntExtra(Intents.COLLECTION_ID, -1)
                    viewModel.getFavouriteCollection(collectionId) { navigateTo<FavouritesActivity>() }
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