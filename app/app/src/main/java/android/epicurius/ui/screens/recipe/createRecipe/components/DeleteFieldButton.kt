package android.epicurius.ui.screens.recipe.createRecipe.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteFieldButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete field", tint = Color.Red)
    }
}
