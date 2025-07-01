package android.epicurius.ui.screens.search.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FiltersIcon(onClick: () -> Unit, enableButtons: Boolean) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 8.dp),
        enabled = enableButtons,
    ) {
        Icon(
            imageVector = Icons.Default.DensityMedium,
            contentDescription = "Filter icon",
            modifier = Modifier
                .size(19.dp)
                .padding(end = 4.dp)
        )
        Text("Filters", fontSize = 15.sp)
    }
}