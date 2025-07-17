package android.epicurius.ui.screens.recipe.createRecipe.components

import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun NutritionalInfoComponent(
    calories: String,
    onCaloriesChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    fat: String,
    onFatChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    enableButtons: Boolean
) {
    var expandNutritionalInfo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp)
    ) {
        TextButton(
            onClick = { expandNutritionalInfo = !expandNutritionalInfo }
        ) {
            Text(
                text = if (expandNutritionalInfo) "- Hide nutritional info"
                    else "+ Add nutritional info",
                color = Lilac
            )
        }
    }
    AnimatedVisibility(visible = expandNutritionalInfo) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            NumberTextField(
                value = calories,
                onValueChange = { onCaloriesChange(it) },
                modifier = Modifier.padding(horizontal = 30.dp),
                enabled = enableButtons,
                label = "Calories (kcal)"
            )
            NumberTextField(
                value = protein,
                onValueChange = { onProteinChange(it) },
                modifier = Modifier.padding(horizontal = 30.dp),
                enabled = enableButtons,
                label = "Protein (g)"
            )
            NumberTextField(
                value = fat,
                onValueChange = { onFatChange(it) },
                modifier = Modifier.padding(horizontal = 30.dp),
                enabled = enableButtons,
                label = "Fat (g)"
            )
            NumberTextField(
                value = carbs,
                onValueChange = { onCarbsChange(it) },
                modifier = Modifier.padding(horizontal = 30.dp),
                enabled = enableButtons,
                label = "Carbohydrates (g)"
            )
        }
    }
}