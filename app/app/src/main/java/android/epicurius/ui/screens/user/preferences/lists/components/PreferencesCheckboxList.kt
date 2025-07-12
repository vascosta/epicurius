package android.epicurius.ui.screens.user.preferences.lists.components

import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun <T> PreferencesCheckboxList(
    title: String,
    description: String,
    items: List<T>,
    checkboxStates: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    enableButtons: Boolean,
    displayName: (T) -> String
) {
    Text(
        text = title,
        modifier = Modifier.padding(bottom = 8.dp),
        color = DarkPurple,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = description,
        modifier = Modifier.padding(bottom = 8.dp),
        color = Lilac,
        style = MaterialTheme.typography.bodyMedium
    )
    items.forEachIndexed { idx, item ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkboxStates[idx],
                onCheckedChange = { onCheckedChange(idx, it) },
                enabled = enableButtons
            )
            Text(displayName(item))
        }
    }
}
