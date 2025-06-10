package android.epicurius.ui.screens.user.settings.components

import android.epicurius.ui.screens.utils.LoadingSpinner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    var showLoadingSpinner by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            onClick()
            showLoadingSpinner = true
        },
        modifier = Modifier.padding(start = 15.dp, end = 15.dp),
        enabled = enabled
    ) {
        if (!showLoadingSpinner || enabled) {
            Text(text)
        }
        else {
            LoadingSpinner(Modifier.size(30.dp))
        }
    }
}