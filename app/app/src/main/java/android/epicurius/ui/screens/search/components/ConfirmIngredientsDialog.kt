package android.epicurius.ui.screens.search.components

import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
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
    ingredientsState: LoadState<List<String>>,
    onConfirmIngredients: (ingredients: List<String>) -> Unit,
    onIngredientsClear: () -> Unit,
    onCloseDialog: () -> Unit,
    enableButtons: Boolean
) {
    var ingredientsList by remember { mutableStateOf(listOf<String>()) }
    var newIngredient by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            onIngredientsClear()
            onCloseDialog()
        },
        confirmButton = {
            Button(
                onClick = {
                    onIngredientsClear()
                    onCloseDialog()
                },
                enabled = enableButtons
            ) { Text("Cancel") }
            Button(
                onClick = {
                    onConfirmIngredients(ingredientsList)
                    onCloseDialog()
                },
                enabled = enableButtons
            ) { Text("OK") }
        },
        title = { Text("Confirm Ingredients") },
        text = {
            LoadStateRenderer(
                loadState = ingredientsState,
                content = { ingredients ->
                    if (ingredientsState is Loaded && ingredients.isNotEmpty()) {
                        ingredientsList = ingredients
                        Column {
                            ingredientsList.forEach { ingredient ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = true,
                                        onCheckedChange = {
                                            ingredientsList = ingredientsList - ingredient
                                        }
                                    )
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = newIngredient,
                                    onValueChange = { newIngredient = it },
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .padding(end = 8.dp),
                                    enabled = true,
                                    label = "Add Ingredient"
                                )
                                Button(
                                    onClick = {
                                        if (newIngredient.isNotBlank()) {
                                            ingredientsList += newIngredient
                                            newIngredient = ""
                                        }
                                    },
                                    modifier = Modifier.padding(start = 8.dp),
                                    enabled = enableButtons
                                ) { Text("Add") }
                            }
                        }
                    } else if (ingredientsState is Loaded) Text("No ingredients found.")
                }
            )
        }
    )
}