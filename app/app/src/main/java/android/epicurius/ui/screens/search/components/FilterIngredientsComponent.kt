package android.epicurius.ui.screens.search.components

import android.epicurius.ui.screens.recipe.createRecipe.components.AddFieldButton
import android.epicurius.ui.screens.recipe.createRecipe.components.DeleteFieldButton
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterIngredientsComponent(
    ingredients: List<String>,
    onIngredientsChange: (List<String>) -> Unit = {},
    enableButtons: Boolean
) {
    var expandIngredientFields by remember { mutableStateOf(false) }
    val canAddField = ingredients.isEmpty() || ingredients.last().isNotBlank()
    val ingredientsList = ingredients.toMutableList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp)
    ) {
        TextButton(
            onClick = { expandIngredientFields = !expandIngredientFields },
            enabled = enableButtons
        ) {
            Text(
                if (expandIngredientFields) "- Hide Ingredients"
                else "+ Add Ingredients"
            )
            if (ingredients.isEmpty() && expandIngredientFields)
                onIngredientsChange(ingredients.toMutableList() + "")
        }
    }
    AnimatedVisibility(visible = expandIngredientFields) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ingredients.forEachIndexed { index, ingredient ->
                val updatedList = ingredients.toMutableList()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = ingredient,
                        onValueChange = { newName ->
                            updatedList[index] = newName
                            onIngredientsChange(updatedList)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        enabled = enableButtons,
                        label = "Ingredient"
                    )
                    DeleteFieldButton(
                        onClick = {
                            updatedList.removeAt(index)
                            onIngredientsChange(updatedList)
                        },
                        enabled = enableButtons
                    )
                }
            }
            AddFieldButton(
                onClick = {
                    if (!canAddField) return@AddFieldButton
                    onIngredientsChange(ingredientsList + "")
                },
                modifier = Modifier.padding(top = 8.dp),
                enabled = enableButtons,
                text = "Add Ingredient"
            )
        }
    }
}
