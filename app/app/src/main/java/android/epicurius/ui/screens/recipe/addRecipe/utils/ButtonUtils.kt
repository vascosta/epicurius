package android.epicurius.ui.screens.recipe.addRecipe.utils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddFieldButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add field")
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}

@Composable
fun DeleteFieldButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete field", tint = Color.Red)
    }
}
