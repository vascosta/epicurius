package android.epicurius.ui.screens.recipe.confirmIngredients.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SubstituteIngredientsAlertDialog(
    substitutes: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Substitute Ingredients") },
        text = {
            Column {
                if (substitutes. isNotEmpty()) {
                    substitutes.forEach {
                        Text(
                            text = "• $it",
                            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                        )
                    }
                } else {
                    Text(
                        text = "No substitutes available for this ingredient",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}
