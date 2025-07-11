package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.createRecipe.components.DividerComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.IngredientsComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.InstructionsComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.NutritionalInfoComponent
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.utils.dropdownMenu.DropdownMenuComponent
import android.epicurius.ui.screens.utils.FormTextField
import android.epicurius.ui.screens.utils.dropdownMenu.MultiSelectDropdownMenuComponent
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
import androidx.compose.ui.unit.dp
import kotlin.collections.map

@Composable
fun EditRecipeDialog(
    recipe: Recipe,
    onEditRecipe: (
        name: String?,
        description: String?,
        servings: Int?,
        preparationTime: Int?,
        cuisine: Cuisine?,
        mealType: MealType?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?,
        ingredients: List<Ingredient>?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions?
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onDismissRequest: () -> Unit = {},
    enableButtons: Boolean
) {
    var name by remember { mutableStateOf(recipe.name) }
    var description by remember { mutableStateOf(recipe.description) }
    var preparationTime by remember { mutableStateOf(recipe.preparationTime.toString()) }
    var servings by remember { mutableStateOf(recipe.servings.toString()) }
    var mealType by remember { mutableStateOf(recipe.mealType.displayName) }
    var cuisine by remember { mutableStateOf(recipe.cuisine.displayName) }
    var intolerances by remember {
        mutableStateOf(recipe.intolerances.map { it.displayName })
    }
    var diets by remember {
        mutableStateOf(recipe.diets.map { it.displayName })
    }
    var ingredients by remember { mutableStateOf(recipe.ingredients.map { it.toIngredientComponent() })
    }
    var calories by remember { mutableStateOf(recipe.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(recipe.protein?.toString() ?: "") }
    var fat by remember { mutableStateOf(recipe.fat?.toString() ?: "") }
    var carbs by remember { mutableStateOf(recipe.carbs?.toString() ?: "") }
    var steps by remember { mutableStateOf(recipe.instructions.steps.values.map { it }) }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismissRequest },
        confirmButton = {
            TextButton(
                onClick = {
                    val intolerancesList =
                        intolerances.map {
                            Intolerance.fromDisplayName(it)
                        }.toSet()
                    val dietsList = diets.map { Diet.fromDisplayName(it) }.toSet()
                    val ingredientsList = ingredients.map { it.toIngredient() }

                    val stepsMap = steps.mapIndexed { index, step ->
                        (index + 1).toString() to step
                    }.toMap()

                    onEditRecipe(
                        if (name == recipe.name) null else name,
                        if (description == recipe.description) null else description,
                        if (servings == recipe.servings.toString()) null else servings.toIntOrNull(),
                        if (preparationTime == recipe.preparationTime.toString()) null else preparationTime.toIntOrNull(),
                        if (cuisine == recipe.cuisine.displayName) null
                        else Cuisine.valueOf(cuisine.uppercase().replace(Regex("[\\s-]"), "_")),
                        if (mealType == recipe.mealType.displayName) null
                        else MealType.valueOf(mealType.uppercase().replace(Regex("[\\s-]"), "_")),
                        if (intolerancesList == recipe.intolerances) null else intolerancesList,
                        if (dietsList == recipe.diets) null else dietsList,
                        if (ingredients == recipe.ingredients.map { it.toIngredientComponent() }) null
                        else ingredientsList,
                        if (calories == recipe.calories?.toString()) null
                        else calories.toIntOrNull(),
                        if (protein == recipe.protein?.toString()) null
                        else protein.toIntOrNull(),
                        if (fat == recipe.fat?.toString()) null
                        else fat.toIntOrNull(),
                        if (carbs == recipe.carbs?.toString()) null
                        else carbs.toIntOrNull(),
                        if (steps == recipe.instructions.steps.values.map { it }) null
                        else Instructions(stepsMap)
                    )
                    onDismissRequest()
                },
                enabled = enableButtons && name.isNotEmpty() && description.isNotEmpty() &&
                preparationTime.isNotEmpty() && servings.isNotEmpty() && cuisine.isNotEmpty() &&
                mealType.isNotEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text("Cancel") }
        },
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
                    enabled = enableButtons
                )
                FormTextField(
                    parameterName = "Description",
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.height(56.dp),
                    enabled = enableButtons
                )
                NumberLineTextField(
                    parameterName = "Duration (min)",
                    value = preparationTime,
                    onValueChange = { if (isValidForNumberTextField(it)) preparationTime = it },
                    enabled = enableButtons
                )
                NumberLineTextField(
                    parameterName = "Serving (px)",
                    value = servings,
                    onValueChange = { if (isValidForNumberTextField(it)) servings = it },
                    enabled = enableButtons
                )
                DropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    value = mealType,
                    onValueChange = { mealType = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = enableButtons,
                    label = "Meal Type"
                )
                DropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = enableButtons,
                    label = "Cuisine"
                )
                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = { intolerances = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = enableButtons,
                    label = "Intolerances"
                )
                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = { diets = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = enableButtons,
                    label = "Diets",
                )
                NutritionalInfoComponent(
                    calories = calories,
                    onCaloriesChange = { if (isValidForNumberTextField(it)) calories = it },
                    protein = protein,
                    onProteinChange = { if (isValidForNumberTextField(it)) protein = it },
                    fat = fat,
                    onFatChange = { if (isValidForNumberTextField(it)) fat = it },
                    carbs = carbs,
                    onCarbsChange = { if (isValidForNumberTextField(it)) carbs = it },
                    enableButtons = enableButtons
                )
                DividerComponent()
                IngredientsComponent(
                    ingredients = ingredients,
                    onIngredientsChange = { ingredients = it },
                    enabled = enableButtons
                )
                DividerComponent()
                InstructionsComponent(
                    steps = steps,
                    onStepsChange = { steps = it },
                    enabled = enableButtons
                )
            }
        },
        containerColor = DarkGreen,
    )
}