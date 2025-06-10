package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditCollectionDialog(
    collectionName: String,
    collectionId: Int,
    onDismiss: () -> Unit,
    onEditCollection: (collectionId: Int, collectionName: String) -> Unit,
    enableButtons: Boolean
) {
    var newCollectionName by rememberSaveable { mutableStateOf(collectionName) }
    var showLoadingSpinner by rememberSaveable { mutableStateOf(!enableButtons) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            ) { Text("Cancel") }
            TextButton(
                onClick = {
                    onEditCollection(collectionId, newCollectionName)
                    showLoadingSpinner = true
                },
                enabled = enableButtons
            ) {
                if (!showLoadingSpinner || enableButtons) { Text("Edit") }
                else { LoadingSpinner(Modifier.size(30.dp)) }
            }
        },
        title = { Text("Edit Collection") },
        text = {
            Column {
                Text("Edit your collection name:")
                Spacer(Modifier.width(10.dp))
                TextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enableButtons,
                    label = "Collection Name"
                )
            }
        }
    )
}