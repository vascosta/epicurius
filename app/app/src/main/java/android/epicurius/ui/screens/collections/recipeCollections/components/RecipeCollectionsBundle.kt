package android.epicurius.ui.screens.collections.recipeCollections.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.LoadState

data class RecipeCollectionsStateBundle(
    val collectionsToAddRecipeState: LoadState<List<CollectionProfile>>,
    val collectionsToRemoveRecipeState: LoadState<List<CollectionProfile>>
)