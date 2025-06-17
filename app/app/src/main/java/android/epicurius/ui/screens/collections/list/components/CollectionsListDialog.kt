package android.epicurius.ui.screens.collections.list.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun CollectionsListDialog(
    recipeId: Int,
    isInCollection: Boolean,
    collectionsStateBundle: CollectionsStateBundle?,
    onDismissRequest: () -> Unit,
    onCollectionChange: () -> Unit,
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
    onCollectionsRequest: (recipeId: Int) -> Unit,
    enableButtons: Boolean
) {
    if (collectionsStateBundle != null) {
        var showLoadingSpinnerButtons by remember { mutableStateOf(!enableButtons) }
        var showLoadingSpinnerOnLoadMore by remember { mutableStateOf(!enableButtons) }
        var selectedTabIndex by remember { mutableIntStateOf(0) }.apply {
            if (!isInCollection) { 0 } else { 1 }
        }
        var selectedCollectionsIds = remember { mutableStateListOf<Int>() }

        LaunchedEffect(selectedTabIndex) {
            selectedCollectionsIds.clear()
            if (
                collectionsStateBundle.collectionsToAddRecipeState is Idle ||
                collectionsStateBundle.collectionsToRemoveRecipeState is Idle
                )
            {
                onCollectionsRequest(recipeId)
            }
        }
        AlertDialog(
            onDismissRequest = {
                if (collectionsStateBundle.collectionsToAddRecipeState is Loaded ||
                    collectionsStateBundle.collectionsToRemoveRecipeState is Loaded) {
                    onDismissRequest()
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCollectionsRequest(recipeId)
                        showLoadingSpinnerOnLoadMore = true
                    },
                    enabled = enableButtons
                ) {
                    if (!showLoadingSpinnerOnLoadMore || enableButtons) {
                        Text("Load More")
                    }
                    else {
                        LoadingSpinner(Modifier.size(30.dp))
                    }
                }
                CollectionsListDialogButton(
                    recipeId = recipeId,
                    collectionsStateBundle = collectionsStateBundle,
                    selectedTabIndex = selectedTabIndex,
                    selectedCollectionsIds = selectedCollectionsIds,
                    onAddRecipeToCollections = onAddRecipeToCollections,
                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                    enabled = enableButtons,
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissRequest() },
                    enabled = enableButtons
                ) { Text(text = "Cancel") }
            },
            title = { Text("Favourites") },
            text = {
                Column {
                    CollectionsListDialogTab(
                        selectedTabIndex = selectedTabIndex,
                        onCollectionsToAdd = { selectedTabIndex = 0 },
                        onCollectionsToRemove = { selectedTabIndex = 1 },
                        enabled = enableButtons
                    )
                    if (selectedTabIndex == 0) {
                        Text("Choose the collections to add the recipe")
                        LoadStateRenderer(
                            loadState = collectionsStateBundle.collectionsToAddRecipeState,
                            content = { collectionsList ->
                                Spacer(Modifier.height(10.dp))
                                if (collectionsList.isNotEmpty()) {
                                    collectionsList.forEachIndexed { index, collection ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedCollectionsIds.contains(collection.id),
                                                onCheckedChange = { isChecked ->
                                                    if (isChecked) {
                                                        selectedCollectionsIds.add(collection.id)
                                                    }
                                                    else {
                                                        selectedCollectionsIds.remove(collection.id)
                                                    }
                                                },
                                                enabled = enableButtons
                                            )
                                            if (!showLoadingSpinnerButtons || enableButtons) {
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
                                else if (collectionsStateBundle.collectionsToAddRecipeState is Loaded) {
                                    Text(
                                        text = "No collections available to add the recipe",
                                        modifier = Modifier.padding(10.dp),
                                        color = Color.Gray
                                    )
                                }
                            }
                        )
                    }
                    else {
                        Text("Choose a collection to remove the recipes")
                        LoadStateRenderer(
                            loadState = collectionsStateBundle.collectionsToRemoveRecipeState,
                            content = { collectionsList ->
                                Spacer(Modifier.height(10.dp))
                                if (collectionsList.isNotEmpty()) {
                                    collectionsList.forEachIndexed { index, collection ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedCollectionsIds.contains(collection.id),
                                                onCheckedChange = { isChecked ->
                                                    if (isChecked) {
                                                        selectedCollectionsIds.add(collection.id)
                                                    }
                                                    else {
                                                        selectedCollectionsIds.remove(collection.id)
                                                    }

                                                },
                                                enabled = enableButtons
                                            )
                                            if (!showLoadingSpinnerButtons || enableButtons) {
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
                                else if (collectionsStateBundle.collectionsToAddRecipeState is Loaded) {
                                    Text(
                                        text = "No collections available to remove the recipe",
                                        modifier = Modifier.padding(10.dp),
                                        color = Color.Gray
                                    )
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
