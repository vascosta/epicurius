package android.epicurius.ui.screens.collections.components

import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

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
            ) { Text(text = "Delete", color = Color.Red) }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text(text = "Cancel", color = Lilac) }
        },
        title = { Text(text = "Delete Collection", color = Beige) },
        text = {
            Text(
                text = "Are you sure you want to delete '${collectionName}'?",
                textAlign = TextAlign.Center,
                color = Beige
            )
       },
        containerColor = DarkGreen
    )
}