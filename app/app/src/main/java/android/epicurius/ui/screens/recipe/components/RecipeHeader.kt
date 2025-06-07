package android.epicurius.ui.screens.recipe.components

import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeHeader(
    collectionId: Int?,
    recipeId: Int,
    name: String,
    author: String,
    isInCollection: Boolean,
    collectionsState: LoadState<List<CollectionProfile>>?,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onCollectionsRequest: (Int, Boolean) -> Unit = {_, _ ->},
    enableButtons: Boolean
) {
    var showCollectionsDialog by remember { mutableStateOf(false) }
    var enableStarIcon by remember { mutableStateOf(isInCollection) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MixedText(boldString = "by ", normalString = author)

            IconButton(
                onClick = {
                    if (collectionId != null) {
                        onRemoveRecipeFromCollection(collectionId, recipeId)
                    }
                    else {
                        showCollectionsDialog = true
                    }
                },
                modifier = Modifier.size(24.dp),
                enabled = enableButtons
            ) {
                if (enableButtons) {
                    val painter = if (enableStarIcon) {
                        painterResource(R.drawable.star)
                    } else {
                        painterResource(R.drawable.white_star)
                    }
                    Image(
                        painter = painter,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                else {
                    LoadingSpinner(Modifier.size(30.dp))
                }

                if (showCollectionsDialog) {
                    CollectionsListDialog(
                        recipeId = recipeId,
                        isInCollection = enableStarIcon,
                        collectionsState = collectionsState,
                        onDismissRequest = { showCollectionsDialog = false },
                        onCollectionChange = { enableStarIcon = !enableStarIcon },
                        onAddRecipeToCollection = onAddRecipeToCollection,
                        onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                        onCollectionsRequest = onCollectionsRequest,
                        enableButtons = enableButtons
                    )
                }
            }
        }
    }
}

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
    enableButtons: Boolean
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
                                            enabled = enableButtons
                                        )
                                        if (enableButtons) {
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
                                            enabled = enableButtons
                                        )
                                        if (enableButtons) {
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