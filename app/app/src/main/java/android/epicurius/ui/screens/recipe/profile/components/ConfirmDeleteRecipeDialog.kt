package android.epicurius.ui.screens.recipe.profile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConfirmDeleteRecipeDialog(
    recipeId: Int,
    onDismissRequest: () -> Unit,
    onConfirmDelete: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this recipe?") },
        confirmButton = {
            TextButton(
                onClick = { onConfirmDelete(recipeId) }
            ) { Text("Yes", color = Color.Red) }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) { Text("No") }
        }
    )
}
