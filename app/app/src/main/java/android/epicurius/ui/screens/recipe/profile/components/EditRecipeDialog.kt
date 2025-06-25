package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.createRecipe.IngredientComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.DividerComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.IngredientsComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.InstructionsComponent
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.FormTextField
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberLineTextField
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditRecipeDialog(
    recipe: Recipe,
    onDismissRequest: () -> Unit,
    onEditRecipe: () -> Unit,
    buttonsEnable: Boolean
) {
    var name by remember { mutableStateOf(recipe.name) }
    var description by remember { mutableStateOf(recipe.description) }
    var duration by remember { mutableStateOf(recipe.preparationTime.toString()) }
    var serving by remember { mutableStateOf(recipe.servings.toString()) }
    var mealType by remember { mutableStateOf(recipe.mealType.displayName) }
    var cuisine by remember { mutableStateOf(recipe.cuisine.displayName) }
    var intolerances by remember {
        mutableStateOf(listOf<String>(
            recipe.intolerances.map { it.displayName }.toString()
        ))
    }
    var diets by remember {
        mutableStateOf(listOf<String>(
            recipe.diets.map { it.displayName }.toString()
        ))
    }
    var ingredients by remember {
        mutableStateOf(
            recipe.ingredients.map {
                IngredientComponent(
                    name = it.name,
                    quantity = it.quantity.toString(),
                    unit = it.unit.displayName
                )
            }
        )
    }
    var instructions by remember { mutableStateOf(recipe.instructions.steps.values.map { it }) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Edit Recipe") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormTextField(
                    parameterName = "Name",
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.height(56.dp),
                    enabled = buttonsEnable
                )
                FormTextField(
                    parameterName = "Description",
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.height(56.dp),
                    enabled = buttonsEnable
                )
                NumberLineTextField(
                    parameterName = "Duration (min)",
                    value = duration,
                    onValueChange = { if (isValidForNumberTextField(it)) duration = it },
                    enabled = buttonsEnable
                )
                NumberLineTextField(
                    parameterName = "Serving (px)",
                    value = serving,
                    onValueChange = { if (isValidForNumberTextField(it)) serving = it },
                    enabled = buttonsEnable
                )
                DropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    value = mealType,
                    onValueChange = { mealType = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Meal Type"
                )
                DropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Cuisine"
                )
                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = { intolerances = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Intolerances"
                )
                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = { diets = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Diets",
                )
                DividerComponent()
                IngredientsComponent(
                    ingredients = ingredients,
                    onIngredientsChange = { ingredients = it },
                    enabled = buttonsEnable
                )
                DividerComponent()
                InstructionsComponent(
                    steps = instructions,
                    onStepsChange = { instructions = it },
                    enabled = buttonsEnable
                )
            }
        },
        containerColor = Color.White,
        confirmButton = {
            TextButton(
                onClick = { onEditRecipe() }
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) { Text("Cancel") }
        }
    )
}