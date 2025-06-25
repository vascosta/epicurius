package android.epicurius.ui.screens.collections.collection.components

import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditCollectionDialog(
    collectionId: Int,
    collectionName: String,
    onEditCollection: (collectionId: Int, collectionName: String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    var newCollectionName by rememberSaveable { mutableStateOf(collectionName) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            ) { Text("Cancel") }
            TextButton(
                onClick = { onEditCollection(collectionId, newCollectionName) },
                enabled = enableButtons
            ) { Text("Edit") }
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