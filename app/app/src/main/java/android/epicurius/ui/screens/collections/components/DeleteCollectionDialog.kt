package android.epicurius.ui.screens.collections.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteCollectionDialog(
    collectionId: Int,
    collectionName: String,
    onCollectionDelete: (collectionId: Int) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    enableButtons: Boolean
) {

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onCollectionDelete(collectionId)
                    onDismissRequest()
                },
                enabled = enableButtons
            ) { Text("Delete", color = Color.Red) }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text("Delete Collection") },
        text = { Text("Are you sure you want to delete '${collectionName}'?") }
    )
}