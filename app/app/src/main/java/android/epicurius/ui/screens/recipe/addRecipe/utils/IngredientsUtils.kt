package android.epicurius.ui.screens.recipe.addRecipe.utils

import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.screens.recipe.addRecipe.IngredientComponent
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsComponent(
    ingredients: List<IngredientComponent>,
    onIngredientsChange: (List<IngredientComponent>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)

        ingredients.forEachIndexed { index, ingredient ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberTextField(
                        parameterName = "Quantity",
                        value = ingredient.quantity,
                        modifier = Modifier.weight(1f)
                    ) { newQuantity ->
                        val updatedList = ingredients.toMutableList()
                        updatedList[index] = updatedList[index].copy(quantity = newQuantity)
                        onIngredientsChange(updatedList)
                    }

                    DropdownMenuComponent(
                        options = IngredientUnit.entries.map { it.name },
                        value = ingredient.unit,
                        onValueChange = { newUnit ->
                            val updatedList = ingredients.toMutableList()
                            updatedList[index] = updatedList[index].copy(unit = newUnit)
                            onIngredientsChange(updatedList)
                        },
                        label = "Unit",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingredient.name,
                        onValueChange = { newName ->
                            val updatedList = ingredients.toMutableList()
                            updatedList[index] = updatedList[index].copy(name = newName)
                            onIngredientsChange(updatedList)
                        },
                        label = { Text("Ingredient") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    DeleteFieldButton(
                        onClick = {
                            val updatedList = ingredients.toMutableList()
                            updatedList.removeAt(index)
                            onIngredientsChange(updatedList)
                        }
                    )
                }
            }
        }

        val canAddField = ingredients.isEmpty() ||
                ingredients.last().name.isNotBlank() &&
                ingredients.last().quantity.isNotBlank() &&
                ingredients.last().unit.isNotBlank()

        AddFieldButton(
            onClick = {
                if (!canAddField) return@AddFieldButton
                onIngredientsChange(ingredients + IngredientComponent("", "", ""))
            },
            modifier = Modifier.padding(top = 8.dp),
            text = "Add Ingredient"
        )
    }
}
