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
    onIngredientsClear: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var ingredientsList by remember { mutableStateOf(listOf<String>()) }
    var newIngredient by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onIngredientsClear() },
        confirmButton = {
            Button(onClick = { onIngredientsClear() }) {
                Text("Cancel")
            }
            Button(onClick = { onConfirm(ingredientsList) }) {
                Text("OK")
            }
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
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
                                    enabled = true
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
                    } else if (ingredientsState is Loaded) {
                        Text("No ingredients found.")
                    }
                }
            )
        }
    )
}