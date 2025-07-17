package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConfirmDeleteRecipeDialog(
    onConfirmDeleteRecipe: () -> Unit = {},
    onDismissRequest: () -> Unit = { },
    enableButtons: Boolean,
) {
    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDeleteRecipe()
                    onDismissRequest()
                },
                enabled = enableButtons
            ) { Text(text = "Yes", color = Color.Red) }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text(text = "No", color = Lilac) }
        },
        title = { Text(text = "Confirm Deletion", color = Lilac) },
        text = { Text(text = "Are you sure you want to delete this recipe?", color = Beige) },
        containerColor = DarkGreen
    )
}
