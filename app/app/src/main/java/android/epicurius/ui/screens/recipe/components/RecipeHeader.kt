package android.epicurius.ui.screens.recipe.components

import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.collections.list.components.CollectionsListDialog
import android.epicurius.ui.screens.collections.list.components.CollectionsStateBundle
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    collectionsStateBundle: CollectionsStateBundle?,
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
    onRemoveRecipeFromCollection: (
        collectionId: Int,
        recipeId: Int
    ) -> Unit,
    onCollectionsClear: () -> Unit,
    onCollectionsRequest: (recipeId: Int) -> Unit,
    enableButtons: Boolean
) {
    var showCollectionsDialog by remember { mutableStateOf(false) }
    var showLoadingSpinnerOnStarIcon by remember { mutableStateOf(false) }

    LaunchedEffect(enableButtons) {
        if (enableButtons) showLoadingSpinnerOnStarIcon = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
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
                        showLoadingSpinnerOnStarIcon = true
                    }
                    else { showCollectionsDialog = true }
                },
                modifier = Modifier.size(24.dp),
                enabled = enableButtons
            ) {
                if (!showLoadingSpinnerOnStarIcon) {
                    val painter =
                        if (isInCollection) painterResource(R.drawable.star)
                        else painterResource(R.drawable.white_star)
                    Image(
                        painter = painter,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                else { LoadingSpinner(Modifier.size(30.dp)) }
                if (showCollectionsDialog) {
                    CollectionsListDialog(
                        recipeId = recipeId,
                        isInCollection = isInCollection,
                        collectionsStateBundle = collectionsStateBundle,
                        onDismissRequest = {
                            if (enableButtons) {
                                showCollectionsDialog = false
                                onCollectionsClear()
                            }
                        },
                        onAddRecipeToCollections = onAddRecipeToCollections,
                        onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                        onCollectionsRequest = onCollectionsRequest,
                        enableButtons = enableButtons
                    )
                }
            }
        }
    }
}
