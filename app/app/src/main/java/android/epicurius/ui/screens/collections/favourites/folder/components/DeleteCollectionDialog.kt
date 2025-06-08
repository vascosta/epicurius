package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeleteCollectionDialog(
    collectionName: String,
    collectionId: Int,
    onCollectionDelete: (collectionId: Int) -> Unit,
    onDismissRequest: () -> Unit,
    buttonsEnable: Boolean
) {

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Delete Collection") },
        text = { Text("Are you sure you want to delete '${collectionName}'?") },
        confirmButton = {
            TextButton(
                onClick = { onCollectionDelete(collectionId) },
                enabled = buttonsEnable
            ) {
                if (buttonsEnable) { Text("Delete", color = Color.Red) }
                else { LoadingSpinner(Modifier.size(30.dp)) }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = buttonsEnable
            ) { Text("Cancel") }
        }
    )
}