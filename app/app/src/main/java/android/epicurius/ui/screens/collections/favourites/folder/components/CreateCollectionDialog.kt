package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCollectionCreate: (collectionName: String) -> Unit,
    enableButtons: Boolean
) {
    var collectionName by remember { mutableStateOf("") }
    var showLoadingSpinner by remember { mutableStateOf(!enableButtons) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            ) { Text("Cancel") }
            TextButton(
                onClick = { onCollectionCreate(collectionName) },
                enabled = enableButtons
            ) {
                if (!showLoadingSpinner || enableButtons) { Text("Create") }
                else { LoadingSpinner(Modifier.size(30.dp)) }
            }
        },
        title = { Text("Create Collection") },
        text = {
            Column {
                Text("Enter the name of the new collection:")
                TextField(
                    value = collectionName,
                    onValueChange = { collectionName = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enableButtons,
                    label = "Collection Name"
                )
            }
        }
    )
}
