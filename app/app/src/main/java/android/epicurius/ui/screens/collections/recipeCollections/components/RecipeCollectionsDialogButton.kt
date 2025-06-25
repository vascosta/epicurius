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
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
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
                        recipeId,
                        recipeCollectionsStateBundle.collectionsToAddRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds }
                    )
                }
                else {
                    onRemoveRecipeFromCollections(
                        recipeId,
                        recipeCollectionsStateBundle.collectionsToRemoveRecipeState.value.getValueOrThrow()
                            .filter { it.id in selectedCollectionsIds }
                    )
                }
            }
        },
        enabled = enabled
    ) { if (selectedTabIndex == 0) Text("Add") else Text("Remove") }
}