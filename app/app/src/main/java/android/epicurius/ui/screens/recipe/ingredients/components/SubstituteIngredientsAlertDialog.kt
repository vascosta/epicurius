package android.epicurius.ui.screens.recipe.ingredients.components

import android.epicurius.domain.recipe.Ingredient
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubstituteIngredientsAlertDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Substitute Ingredients") },
        text = {
            val substitutes = listOf(ingredient.name) // Replace with actual substitute logic
            substitutes.forEach {
                Text(text = "• $it", modifier = Modifier.padding(start = 10.dp, bottom = 10.dp))
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}