package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.R
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
    onShowCollectionsDialog: () -> Unit = {},
    enableButtons: Boolean,
) {
    IconButton(
        onClick = { onShowCollectionsDialog() },
        modifier = Modifier.size(24.dp),
        enabled = enableButtons
    ) {
        val painter = painterResource(R.drawable.white_star)
        Image(
            painter = painter,
            contentDescription = "Favorites",
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )
    }
}
