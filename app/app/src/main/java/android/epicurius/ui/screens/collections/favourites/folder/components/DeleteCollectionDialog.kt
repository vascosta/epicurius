package android.epicurius.ui.screens.collections.favourites.folder.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteCollectionDialog(
    collectionName: String,
    collectionId: Int,
    onDelete: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Delete Collection") },
        text = { Text("Are you sure you want to delete '${collectionName}'?") },
        confirmButton = {
            TextButton(onClick = {
                onDelete(collectionId)
                onDismissRequest()
            }) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}