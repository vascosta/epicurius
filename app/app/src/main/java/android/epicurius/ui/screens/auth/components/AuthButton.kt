package android.epicurius.ui.screens.auth.components

import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthButton(
    onClick: () -> Unit,
    enabled: Boolean,
    label: String,
) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(10.dp),
        enabled = enabled
    ) {
        if (enabled) {
            Text(label)
        }
        else {
            LoadingSpinner(Modifier.size(30.dp))
        }
    }
}