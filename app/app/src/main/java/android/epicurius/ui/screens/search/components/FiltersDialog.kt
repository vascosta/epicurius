package android.epicurius.ui.screens.search.components

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.screens.recipe.createRecipe.components.DividerComponent
import android.epicurius.ui.screens.utils.dropdownMenu.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FiltersDialog(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    cuisine: List<String>,
    onCuisineChange: (List<String>) -> Unit,
    mealType: List<String>,
    onMealTypeChange: (List<String>) -> Unit,
    ingredients: List<String>,
    onIngredientsChange: (List<String>) -> Unit,
    intolerances: List<String>,
    onIntolerancesChange: (List<String>) -> Unit,
    diets: List<String>,
    onDietsChange: (List<String>) -> Unit,
    servings: String,
    onServingsChange: (String) -> Unit,
    minCalories: String,
    onMinCaloriesChange: (String) -> Unit,
    maxCalories: String,
    onMaxCaloriesChange: (String) -> Unit,
    minCarbs: String,
    onMinCarbsChange: (String) -> Unit,
    maxCarbs: String,
    onMaxCarbsChange: (String) -> Unit,
    minFat: String,
    onMinFatChange: (String) -> Unit,
    maxFat: String,
    onMaxFatChange: (String) -> Unit,
    minProtein: String,
    onMinProteinChange: (String) -> Unit,
    maxProtein: String,
    onMaxProteinChange: (String) -> Unit,
    minTime: String,
    onMinTimeChange: (String) -> Unit,
    maxTime: String,
    onMaxTimeChange: (String) -> Unit,
    showAuthorRecipes: Boolean,
    onShowAuthorRecipesChange: (Boolean) -> Unit,
    enableButtons: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        title = { Text("Filters") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MultiSelectDropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    values = cuisine,
                    onValuesChange = onCuisineChange,
                    enabled = enableButtons,
                    label = "Cuisine"
                )
                MultiSelectDropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    values = mealType,
                    onValuesChange = onMealTypeChange,
                    enabled = enableButtons,
                    label = "Meal Type"
                )
                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = onIntolerancesChange,
                    enabled = enableButtons,
                    label = "Intolerances"
                )
                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = onDietsChange,
                    enabled = enableButtons,
                    label = "Diets"
                )
                NumberTextField(
                    value = servings,
                    onValueChange = onServingsChange,
                    enabled = enableButtons,
                    label = "Servings"
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minCalories,
                        onValueChange = onMinCaloriesChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = enableButtons,
                        label = "Min Calories",
                    )
                    NumberTextField(
                        value = maxCalories,
                        onValueChange = onMaxCaloriesChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enableButtons,
                        label = "Max Calories"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minCarbs,
                        onValueChange = onMinCarbsChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = enableButtons,
                        label = "Min Carbs"
                    )
                    NumberTextField(
                        value = maxCarbs,
                        onValueChange = onMaxCarbsChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enableButtons,
                        label = "Max Carbs"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minFat,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onValueChange = onMinFatChange,
                        enabled = enableButtons,
                        label = "Min Fat"
                    )
                    NumberTextField(
                        value = maxFat,
                        onValueChange = onMaxFatChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enableButtons,
                        label = "Max Fat"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minProtein,
                        onValueChange = onMinProteinChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = enableButtons,
                        label = "Min Protein"
                    )
                    NumberTextField(
                        value = maxProtein,
                        onValueChange = onMaxProteinChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enableButtons,
                        label = "Max Protein"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minTime,
                        onValueChange = onMinTimeChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = enableButtons,
                        label = "Min Time",
                    )
                    NumberTextField(
                        value = maxTime,
                        onValueChange = onMaxTimeChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enableButtons,
                        label = "Max Time"
                    )
                }

                DividerComponent()
                FilterIngredientsComponent(
                    ingredients = ingredients,
                    onIngredientsChange = onIngredientsChange,
                    enableButtons = enableButtons
                )
                DividerComponent()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showAuthorRecipes,
                        onCheckedChange = onShowAuthorRecipesChange,
                        enabled = enableButtons
                    )
                    Text("Show Author Recipes", modifier = Modifier.weight(1f))
                }
            }
        }
    )
}

fun clearFilters(
    onCuisineChange: (List<String>) -> Unit,
    onMealTypeChange: (List<String>) -> Unit,
    onIngredientsChange: (List<String>) -> Unit,
    onIntolerancesChange: (List<String>) -> Unit,
    onDietsChange: (List<String>) -> Unit,
    onServingsChange: (String) -> Unit,
    onMinCaloriesChange: (String) -> Unit,
    onMaxCaloriesChange: (String) -> Unit,
    onMinCarbsChange: (String) -> Unit,
    onMaxCarbsChange: (String) -> Unit,
    onMinFatChange: (String) -> Unit,
    onMaxFatChange: (String) -> Unit,
    onMinProteinChange: (String) -> Unit,
    onMaxProteinChange: (String) -> Unit,
    onMinTimeChange: (String) -> Unit,
    onMaxTimeChange: (String) -> Unit,
    onShowAuthorRecipesChange: (Boolean) -> Unit
) {
    onCuisineChange(emptyList())
    onMealTypeChange(emptyList())
    onIngredientsChange(emptyList())
    onIntolerancesChange(emptyList())
    onDietsChange(emptyList())
    onIngredientsChange(emptyList())
    onServingsChange("")
    onMinCaloriesChange("")
    onMaxCaloriesChange("")
    onMinCarbsChange("")
    onMaxCarbsChange("")
    onMinFatChange("")
    onMaxFatChange("")
    onMinProteinChange("")
    onMaxProteinChange("")
    onMinTimeChange("")
    onMaxTimeChange("")
    onShowAuthorRecipesChange(false)
}

@Preview
@Composable
fun FiltersDialogPreview() {
    FiltersDialog(
        onDismiss = {},
        onCancel = {},
        cuisine = emptyList(),
        onCuisineChange = {},
        mealType = emptyList(),
        onMealTypeChange = {},
        ingredients = emptyList(),
        onIngredientsChange = {},
        intolerances = emptyList(),
        onIntolerancesChange = {},
        diets = emptyList(),
        onDietsChange = {},
        servings = "",
        onServingsChange = {},
        minCalories = "",
        onMinCaloriesChange = {},
        maxCalories = "",
        onMaxCaloriesChange = {},
        minCarbs = "",
        onMinCarbsChange = {},
        maxCarbs = "",
        onMaxCarbsChange = {},
        minFat = "",
        onMinFatChange = {},
        maxFat = "",
        onMaxFatChange = {},
        minProtein = "",
        onMinProteinChange = {},
        maxProtein = "",
        onMaxProteinChange = {},
        minTime = "",
        onMinTimeChange = {},
        maxTime = "",
        onMaxTimeChange = {},
        showAuthorRecipes = false,
        onShowAuthorRecipesChange = {},
        enableButtons = true
    )
}