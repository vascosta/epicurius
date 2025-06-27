package android.epicurius.ui.screens.recipe.profile.components

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
            ) { Text("Yes", color = Color.Red) }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text("No") }
        },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this recipe?") },
    )
}
