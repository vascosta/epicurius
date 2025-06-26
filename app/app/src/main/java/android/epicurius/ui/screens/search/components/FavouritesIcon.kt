package android.epicurius.ui.screens.search.components

import android.epicurius.R
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesIcon(
    recipe: Recipe,
    collectionId: Int?,
    onShowCollectionDialog: () -> Unit,
    onRemoveRecipeFromCollection: (
        collectionId: Int,
        recipe: Int
    ) -> Unit,
    enableStarIcon: Boolean,
    enableButtons: Boolean,
) {
    IconButton(
        onClick = {
            if (collectionId != null) {
                onRemoveRecipeFromCollection(collectionId, recipe.id)
            }
            else onShowCollectionDialog()
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
    }
}
