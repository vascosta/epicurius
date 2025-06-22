package android.epicurius.ui.screens.search.components

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FiltersDialog(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    mealType: List<String>,
    onMealTypeChange: (List<String>) -> Unit,
    cuisine: List<String>,
    onCuisineChange: (List<String>) -> Unit,
    intolerances: List<String>,
    onIntolerancesChange: (List<String>) -> Unit,
    diets: List<String>,
    onDietsChange: (List<String>) -> Unit,
    preparationTime: String,
    onPreparationTimeChange: (String) -> Unit,
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
    buttonsEnable: Boolean
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
        title = {
            Text("Filters")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MultiSelectDropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    values = mealType,
                    onValuesChange = onMealTypeChange,
                    enabled = buttonsEnable,
                    label = "Meal Type"
                )
                MultiSelectDropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    values = cuisine,
                    onValuesChange = onCuisineChange,
                    enabled = buttonsEnable,
                    label = "Cuisine"
                )
                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = onIntolerancesChange,
                    enabled = buttonsEnable,
                    label = "Intolerances"
                )
                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = onDietsChange,
                    enabled = buttonsEnable,
                    label = "Diets"
                )
                NumberTextField(
                    value = preparationTime,
                    onValueChange = onPreparationTimeChange,
                    enabled = buttonsEnable,
                    label = "Preparation Time"
                )
                NumberTextField(
                    value = servings,
                    onValueChange = onServingsChange,
                    enabled = buttonsEnable,
                    label = "Servings"
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minCalories,
                        onValueChange = onMinCaloriesChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = buttonsEnable,
                        label = "Min Calories",
                    )
                    NumberTextField(
                        value = maxCalories,
                        onValueChange = onMaxCaloriesChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonsEnable,
                        label = "Max Calories"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minCarbs,
                        onValueChange = onMinCarbsChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = buttonsEnable,
                        label = "Min Carbs"
                    )
                    NumberTextField(
                        value = maxCarbs,
                        onValueChange = onMaxCarbsChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonsEnable,
                        label = "Max Carbs"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minFat,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onValueChange = onMinFatChange,
                        enabled = buttonsEnable,
                        label = "Min Fat"
                    )
                    NumberTextField(
                        value = maxFat,
                        onValueChange = onMaxFatChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonsEnable,
                        label = "Max Fat"
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField(
                        value = minProtein,
                        onValueChange = onMinProteinChange,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        enabled = buttonsEnable,
                        label = "Min Protein"
                    )
                    NumberTextField(
                        value = maxProtein,
                        onValueChange = onMaxProteinChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonsEnable,
                        label = "Max Protein"
                    )
                }
            }
        }
    )
}