package android.epicurius.ui.screens.collections.components

import android.R
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CreateCollectionDialog(
    onCollectionCreate: (collectionName: String) -> Unit = {},
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    var collectionName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            ) { Text(text = "Cancel", color = Lilac) }
            TextButton(
                onClick = {
                    onCollectionCreate(collectionName)
                    onDismiss()
                },
                enabled = enableButtons
            ) {
                Text(text = "Create", color = Lilac)
            }
        },
        title = { Text(text = "Create Collection", color = Beige) },
        text = {
            Column {
                Text(text = "Enter the name of the new collection:", color = Lilac)
                TextField(
                    value = collectionName,
                    onValueChange = { collectionName = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enableButtons,
                    label = "Collection Name"
                )
            }
        },
        containerColor = DarkGreen
    )
}
