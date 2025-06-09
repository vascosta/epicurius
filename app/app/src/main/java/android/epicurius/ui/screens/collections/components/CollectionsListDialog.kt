package android.epicurius.ui.screens.collections.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * REVER
 */

@Composable
fun CollectionsListDialog(
    recipeId: Int,
    isInCollection: Boolean,
    collectionsState: LoadState<List<CollectionProfile>>?,
    onDismissRequest: () -> Unit,
    onCollectionChange: () -> Unit,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onCollectionsRequest: (Int, Boolean) -> Unit,
    buttonsEnable: Boolean
) {
    if (collectionsState != null) {

        LaunchedEffect(collectionsState) {
            onCollectionsRequest(recipeId, isInCollection)
        }

        LoadStateRenderer(
            loadState = collectionsState,
            content = { collectionsList ->
                AlertDialog(
                    onDismissRequest = { onDismissRequest() },
                    title = { Text("Favourites") },
                    text = {
                        var collectionsIds = remember { mutableStateListOf<Int>() }.apply {
                            collectionsList.map { it.id }
                        }
                        Column {
                            if (isInCollection) {
                                Text("Choose a collection to add the recipe")
                                Spacer(Modifier.height(10.dp))
                                collectionsList.forEachIndexed { index, collection ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = false,
                                            onCheckedChange = { isChecked ->
                                                onAddRecipeToCollection(collection.id, recipeId)
                                                collectionsIds.remove(collection.id)
                                                if (collectionsIds.isEmpty()) {
                                                    onCollectionChange()
                                                }
                                            },
                                            enabled = buttonsEnable
                                        )
                                        if (buttonsEnable) {
                                            Text(
                                                text = collection.name,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        else {
                                            LoadingSpinner(Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
                            else {
                                Text("Choose a collection to remove the recipes")
                                Spacer(Modifier.height(10.dp))
                                collectionsList.forEachIndexed { index, collection ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = true,
                                            onCheckedChange = { isChecked ->
                                                onRemoveRecipeFromCollection(collection.id, recipeId)
                                                collectionsIds.remove(collection.id)
                                                if (collectionsIds.isEmpty()) {
                                                    onCollectionChange()
                                                }
                                            },
                                            enabled = buttonsEnable
                                        )
                                        if (buttonsEnable) {
                                            Text(
                                                text = collection.name,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        else {
                                            LoadingSpinner(Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onCollectionsRequest(recipeId, isInCollection) },
                        ) { Text("Load More") }

                    },
                    dismissButton = {
                        TextButton(
                            onClick = { onDismissRequest() }
                        ) { Text(text = "Cancel") }
                    },
                )
            }
        )
    }
}
