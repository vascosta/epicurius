package android.epicurius.ui.screens.collections.collection

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.favourites.FavouritesActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CollectionActivity : EpicuriusActivity() {
    override val viewModel: CollectionViewModel by getViewModel<CollectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            combine(
                viewModel.collectionRecipes,
                viewModel.collectionName
            ) { collectionRecipesState, collectionNameState -> collectionRecipesState to collectionNameState }
                .collectLatest { (collectionRecipesState, collectionNameState) ->
                    val collectionId = intent.getIntExtra(Intents.COLLECTION_ID, -1)
                    if (collectionRecipesState is Idle || collectionNameState is Idle) {
                        viewModel.getCollection(collectionId) { navigateBack() }
                    }
                }
        }
        setContent {
            val collectionNameState = viewModel.collectionName.collectAsState(idle())
            val collectionRecipesState = viewModel.collectionRecipes.collectAsState(idle())
            CollectionScreen(
                isOwner = intent.getBooleanExtra(Intents.IS_COLLECTION_OWNER, false),
                collectionId = intent.getIntExtra(Intents.COLLECTION_ID, -1),
                collectionNameState = collectionNameState.value,
                collectionRecipesState = collectionRecipesState.value,
                onBackButton = { navigateBack() },
                onCollectionEdit = { collectionId: Int, collectionName: String ->
                    viewModel.updateCollection(collectionId, collectionName)
                    { navigateBack() }
                },
                onCollectionDelete = { collectionId: Int ->
                    viewModel.deleteCollection(collectionId)
                    { navigateBack() }
                },
                onRecipeDelete = { collectionId: Int, recipeId: Int ->
                    viewModel.removeRecipeFromCollection(collectionId, recipeId)
                },
                onRecipeRequest = ::navigateToRecipeProfileActivity,
                enableButtons = viewModel.enableButtons
            )
        }
    }

    private fun navigateBack() {
        val sourceActivity = intent.getStringExtra(Intents.SOURCE_ACTIVITY)
        if (sourceActivity == FavouritesActivity::class.java.name) navigateTo<FavouritesActivity>()
        else if (sourceActivity == UserProfileActivity::class.java.name) navigateTo<UserProfileActivity>()
        else finish()
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}