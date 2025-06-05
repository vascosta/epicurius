package android.epicurius.ui.screens.auth.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthButton(label: String, onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(10.dp),
        enabled = enabled
    ) { Text(label) }
}