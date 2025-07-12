package android.epicurius.ui.screens.recipe.createRecipe.components

import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.screens.recipe.createRecipe.IngredientComponent
import android.epicurius.ui.screens.recipe.utils.formattedQuantity
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.LightGreen
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.dropdownMenu.DropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberLineTextField
import android.epicurius.ui.screens.utils.TextField
import android.epicurius.ui.screens.utils.dropdownMenu.SearchDropdownMenuComponent
import android.epicurius.ui.screens.utils.getOrThrow
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsComponent(
    ingredients: List<IngredientComponent>,
    ingredientsResultState: LoadState<List<String>>,
    onIngredientsChange: (ingredients: List<IngredientComponent>) -> Unit = {},
    onSearchIngredients: (partialName: String) -> Unit = {},
    enabled: Boolean
) {
    var isValidProduct by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingredients",
            color = LightGreen,
            style = MaterialTheme.typography.titleMedium
        )
        ingredients.forEachIndexed { index, ingredient ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val updatedList = ingredients.toMutableList()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberLineTextField(
                        parameterName = "Quantity",
                        value = if (ingredient.quantity.isEmpty()) ingredient.quantity
                        else formattedQuantity(ingredient.quantity.toDouble()),
                        onValueChange = { newQuantity ->
                            if (isValidForNumberTextField(newQuantity)) {
                                val formattedQuantity = formattedQuantity(newQuantity.toDouble())
                                updatedList[index] = updatedList[index].copy(quantity = formattedQuantity)
                                onIngredientsChange(updatedList)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = enabled
                    )
                    DropdownMenuComponent(
                        options = IngredientUnit.entries.map { it.name },
                        value = ingredient.unit,
                        onValueChange = { newUnit ->
                            updatedList[index] = updatedList[index].copy(unit = newUnit)
                            onIngredientsChange(updatedList)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = enabled,
                        label = "Unit",
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchDropdownMenuComponent(
                        optionsState = ingredientsResultState,
                        value = ingredient.name,
                        onValueChange = { newName ->
                            updatedList[index] = updatedList[index].copy(name = newName)
                            onIngredientsChange(updatedList)
                            if (ingredientsResultState is Loaded) {
                                val productsResult = ingredientsResultState.getOrThrow()
                                if (productsResult.isNotEmpty())
                                    isValidProduct = productsResult.contains(ingredient.name)
                            }
                        },
                        onIconClick = { onSearchIngredients(ingredient.name) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        enabled = enabled
                    )
                    DeleteFieldButton(
                        onClick = {
                            updatedList.removeAt(index)
                            onIngredientsChange(updatedList)
                        },
                        enabled = enabled
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
            enabled = enabled,
            text = "Add Ingredient"
        )
    }
}
