package android.epicurius.ui.screens.collections.recipeCollections.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.Loaded
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RecipeCollectionsDialogButton(
    recipeId: Int,
    selectedTabIndex: Int,
    selectedCollectionsIds: List<Int>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onAddRecipeToCollections: (
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit = { _, _ -> },
    enabled: Boolean,
) {
    Button(
        onClick = {
            if (
                recipeCollectionsStateBundle.collectionsToAddRecipeState is Loaded &&
                recipeCollectionsStateBundle.collectionsToRemoveRecipeState is Loaded
            ) {
                if (selectedTabIndex == 0) {
                    onAddRecipeToCollections(
                        recipeCollectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds },
                        recipeId
                    )
                }
                else {
                    onRemoveRecipeFromCollections(
                        recipeCollectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds },
                        recipeId
                    )
                }
            }
        },
        enabled = enabled
    ) { if (selectedTabIndex == 0) Text("Add") else Text("Remove") }
}