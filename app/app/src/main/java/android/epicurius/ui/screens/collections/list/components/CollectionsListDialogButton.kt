package android.epicurius.ui.screens.collections.list.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.Loaded
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CollectionsListDialogButton(
    recipeId: Int,
    collectionsStateBundle: CollectionsStateBundle,
    selectedTabIndex: Int,
    selectedCollectionsIds: List<Int>,
    onAddRecipeToCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    onRemoveRecipeFromCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    enabled: Boolean,
) {
    Button(
        onClick = {
            if (
                collectionsStateBundle.collectionsToAddRecipeState is Loaded &&
                collectionsStateBundle.collectionsToRemoveRecipeState is Loaded
            ) {
                if (selectedTabIndex == 0) {
                    onAddRecipeToCollections(
                        collectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds },
                        recipeId
                    )
                }
                else {
                    onRemoveRecipeFromCollections(
                        collectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds },
                        recipeId
                    )
                }
            }
        },
        enabled = enabled
    ) { if (selectedTabIndex == 0) Text("Add") else Text("Remove") }
}