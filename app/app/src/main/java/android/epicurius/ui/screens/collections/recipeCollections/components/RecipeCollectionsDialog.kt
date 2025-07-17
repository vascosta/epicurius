package android.epicurius.ui.screens.collections.recipeCollections.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.TabComponent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RecipeCollectionsDialog(
    recipeId: Int,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle? = null,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = { },
    onDismissRequest: () -> Unit = {},
    enableButtons: Boolean
) {
    if (recipeCollectionsStateBundle != null) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        var selectedCollectionsIds = remember { mutableStateListOf<Int>() }

        LaunchedEffect(selectedTabIndex) {
            selectedCollectionsIds.clear()
            if (
                recipeCollectionsStateBundle.collectionsToAddRecipeState is Idle ||
                recipeCollectionsStateBundle.collectionsToRemoveRecipeState is Idle
                )
                onRecipeCollectionsRequest(recipeId)
        }
        AlertDialog(
            onDismissRequest = {
                if (
                    (recipeCollectionsStateBundle.collectionsToAddRecipeState is Loaded ||
                    recipeCollectionsStateBundle.collectionsToRemoveRecipeState is Loaded
                    ) && enableButtons
                )
                    onDismissRequest()
            },
            confirmButton = {
                RecipeCollectionsDialogButton(
                    recipeId = recipeId,
                    selectedTabIndex = selectedTabIndex,
                    selectedCollectionsIds = selectedCollectionsIds,
                    recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                    onAddRecipeToCollections = onAddRecipeToCollections,
                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                    enabled = enableButtons,
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissRequest() },
                    enabled = enableButtons
                ) { Text(text = "Cancel", color = Lilac) }
            },
            title = { Text(text = "Collections", color = Beige) },
            text = {
                val tabs = listOf("Add", "Remove")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabComponent(
                        tabs = tabs,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                        colorTittleTab = Beige,
                        enabled = enableButtons
                    )
                    Spacer(Modifier.height(5.dp))
                    if (selectedTabIndex == 0) {
                        Text(
                            text = "Choose the collections to add the recipe",
                            color = Lilac,
                            textAlign = TextAlign.Center
                        )
                        LoadStateRenderer(
                            loadState = recipeCollectionsStateBundle.collectionsToAddRecipeState,
                            content = { collections ->
                                Spacer(Modifier.height(10.dp))
                                if (collections.isNotEmpty()) {
                                    collections.forEachIndexed { index, collection ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedCollectionsIds.contains(collection.id),
                                                onCheckedChange = { isChecked ->
                                                    if (isChecked) selectedCollectionsIds.add(collection.id)
                                                    else selectedCollectionsIds.remove(collection.id)
                                                },
                                                enabled = enableButtons
                                            )
                                            Text(
                                                text = collection.name,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onRecipeCollectionsRequest(recipeId) },
                                        enabled = enableButtons
                                    ) { Text("Load More") }
                                }
                                else if (recipeCollectionsStateBundle.collectionsToAddRecipeState is Loaded)
                                    Text(
                                        text = "No collections available to add the recipe",
                                        modifier = Modifier.padding(10.dp),
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                            }
                        )
                    }
                    else {
                        Text(
                            text = "Choose a collection to remove the recipes",
                            color = Lilac,
                            textAlign = TextAlign.Center
                        )
                        LoadStateRenderer(
                            loadState = recipeCollectionsStateBundle.collectionsToRemoveRecipeState,
                            content = { collections ->
                                Spacer(Modifier.height(10.dp))
                                if (collections.isNotEmpty()) {
                                    collections.forEachIndexed { index, collection ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedCollectionsIds.contains(collection.id),
                                                onCheckedChange = { isChecked ->
                                                    if (isChecked) selectedCollectionsIds.add(collection.id)
                                                    else selectedCollectionsIds.remove(collection.id)
                                                },
                                                enabled = enableButtons
                                            )
                                            Text(
                                                text = collection.name,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onRecipeCollectionsRequest(recipeId) },
                                        enabled = enableButtons
                                    ) { Text("Load More") }
                                }
                                else if (recipeCollectionsStateBundle.collectionsToAddRecipeState is Loaded)
                                    Text(
                                        text = "No collections available to remove the recipe",
                                        modifier = Modifier.padding(10.dp),
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                            }
                        )
                    }
                }
            },
            containerColor = DarkGreen
        )
    }
}
