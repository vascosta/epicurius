package android.epicurius.ui.screens.search.camera.components

import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmIngredientsDialog(
    ingredients: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var ingredientsList by remember { mutableStateOf(ingredients) }
    var newIngredient by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirm Ingredients") },
        text = {
            Column {
                ingredientsList.forEach { ingredient ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { ingredientsList = ingredientsList - ingredient }
                        )
                        Text(
                            text = ingredient,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    TextField(
                        value = newIngredient,
                        label = "Add Ingredient",
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp),
                        onValueChange = { newIngredient = it },
                    )
                    Button(
                        onClick = {
                            if (newIngredient.isNotBlank()) {
                                ingredientsList = ingredientsList + newIngredient
                                newIngredient = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Add")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
            Button(onClick = {
                onConfirm(ingredientsList)
                onDismiss()
            }) {
                Text("OK")
            }
        }
    )
}