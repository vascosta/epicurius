package android.epicurius.ui.screens.user.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteAccountDialog(
    onDismissRequest: () -> Unit = {},
    onDeleteConfirmed: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onDeleteConfirmed() }) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        },
        title = { Text("Delete Account") },
        text = { Text("Are you sure you want to delete your account? This action cannot be undone.") }
    )
}
