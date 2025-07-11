package android.epicurius.ui.screens.recipe.confirmIngredients.components

import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
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
    substituteIngredientsState: LoadState<List<String>>,
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismiss() },
        title = { Text("Substitute Ingredients") },
        text = {
            LoadStateRenderer(
                loadState = substituteIngredientsState,
                content = { substituteIngredients ->
                    Column {
                        if (substituteIngredients. isNotEmpty()) {
                            substituteIngredients.forEach {
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
                }
            )
        },
        confirmButton = {
            Button(onClick = onDismiss,
                enabled = enableButtons
            ) { Text("Close") } }
    )
}
