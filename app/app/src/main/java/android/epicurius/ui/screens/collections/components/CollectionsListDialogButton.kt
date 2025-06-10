package android.epicurius.ui.screens.collections.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    var showLoadingSpinner by remember { mutableStateOf(!enabled) }

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
                    showLoadingSpinner = true
                }
                else {
                    onRemoveRecipeFromCollections(
                        collectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow(),
                        collectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds },
                        recipeId
                    )
                    showLoadingSpinner = true
                }
            }
        },
        enabled = enabled
    ) {
        if (!showLoadingSpinner || enabled) {
            if (selectedTabIndex == 0) { Text("Add") }
            else { Text("Remove") }
        }
        else {
            LoadingSpinner(Modifier.size(30.dp))
        }
    }
}