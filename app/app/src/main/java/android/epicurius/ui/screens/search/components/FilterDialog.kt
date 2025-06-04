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
fun FilterDialog(
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
    onMaxProteinChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Filters")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MultiSelectDropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    values = mealType,
                    onValuesChange = onMealTypeChange,
                    label = "Meal Type"
                )

                MultiSelectDropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    values = cuisine,
                    onValuesChange = onCuisineChange,
                    label = "Cuisine"
                )

                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = onIntolerancesChange,
                    label = "Intolerances"
                )

                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = onDietsChange,
                    label = "Diets"
                )

                NumberTextField("Preparation Time", preparationTime, onValueChange = onPreparationTimeChange)
                NumberTextField("Servings", servings, onValueChange = onServingsChange)

                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField("Min Calories", minCalories, modifier = Modifier.fillMaxWidth(0.5f), onValueChange = onMinCaloriesChange)
                    NumberTextField("Max Calories", maxCalories, modifier = Modifier.fillMaxWidth(), onValueChange = onMaxCaloriesChange)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField("Min Carbs", minCarbs, modifier = Modifier.fillMaxWidth(0.5f), onValueChange = onMinCarbsChange)
                    NumberTextField("Max Carbs", maxCarbs, modifier = Modifier.fillMaxWidth(), onValueChange = onMaxCarbsChange)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField("Min Fat", minFat, modifier = Modifier.fillMaxWidth(0.5f), onValueChange = onMinFatChange)
                    NumberTextField("Max Fat", maxFat, modifier = Modifier.fillMaxWidth(), onValueChange = onMaxFatChange)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    NumberTextField("Min Protein", minProtein, modifier = Modifier.fillMaxWidth(0.5f), onValueChange = onMinProteinChange)
                    NumberTextField("Max Protein", maxProtein, modifier = Modifier.fillMaxWidth(), onValueChange = onMaxProteinChange)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}