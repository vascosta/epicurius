package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.Cached
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    enableButtons: Boolean
) {
    var collectionName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Create Collection") },
        text = {
            Column {
                Text("Enter the name of the new collection:")
                TextField(
                    value = collectionName,
                    onValueChange = { collectionName = it },
                    label = "Collection Name",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            ) { Text("Cancel") }
            TextButton(
                onClick = {
                    onCreate(collectionName)
                          },
                enabled = enableButtons
            ) {
                if (enableButtons) {
                    Text("Create")
                }
                else {
                    LoadingSpinner(Modifier.size(30.dp))
                }
            }
        }
    )
}
