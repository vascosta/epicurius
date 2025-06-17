package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.ui.screens.utils.LoadingSpinner
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeleteCollectionDialog(
    collectionName: String,
    collectionId: Int,
    onCollectionDelete: (collectionId: Int) -> Unit,
    onDismissRequest: () -> Unit,
    enableButtons: Boolean
) {
    var showLoadingSpinnerOnDeleteButton by remember { mutableStateOf(false) }

    LaunchedEffect(enableButtons) {
        if (enableButtons && showLoadingSpinnerOnDeleteButton == true) onDismissRequest() // not working need fix
        if (enableButtons) showLoadingSpinnerOnDeleteButton = false
    }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onCollectionDelete(collectionId)
                    showLoadingSpinnerOnDeleteButton = true
                },
                enabled = enableButtons
            ) {
                if (!showLoadingSpinnerOnDeleteButton) Text("Delete", color = Color.Red)
                else LoadingSpinner(Modifier.size(30.dp))
            }
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