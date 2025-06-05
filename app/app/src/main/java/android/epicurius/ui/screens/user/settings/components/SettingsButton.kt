package android.epicurius.ui.screens.user.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButton(text: String) {
    TextButton(
        onClick = {},
        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
    ) { Text(text) }
}