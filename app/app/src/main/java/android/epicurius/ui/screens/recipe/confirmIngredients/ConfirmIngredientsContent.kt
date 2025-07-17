package android.epicurius.ui.screens.recipe.confirmIngredients

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.confirmIngredients.components.InfoDialog
import android.epicurius.ui.screens.recipe.confirmIngredients.components.IngredientTable
import android.epicurius.ui.screens.recipe.confirmIngredients.components.SubstituteIngredientsAlertDialog
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.utils.LoadState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmIngredientsContent(
    recipe: Recipe,
    substituteIngredientsState: LoadState<List<String>>,
    onSubstituteIngredients: (ingredientName: String) -> Unit = {},
    onConfirmIngredients: () -> Unit = {},
    enableButtons: Boolean,
    paddingValues: PaddingValues
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showSubstituteIngredientsDialog by remember { mutableStateOf(false) }

    val checkboxStates = remember { mutableStateListOf<Boolean>().apply {
        repeat(recipe.ingredients.size) { add(false) }
    } }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ingredients:",
                color = DarkPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            IconButton(
                onClick = { showInfoDialog = true },
                enabled = enableButtons
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info Icon",
                )
            }
        }
        if (showInfoDialog)
            InfoDialog(
                boldText = "You don't have an ingredient?",
                normalText = "Don't worry, click on it and find out if we have a substitute ingredient for it",
                onDismissRequest = { showInfoDialog = false }
            )
        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)) {
            Text(
                text = "Name",
                modifier = Modifier.weight(0.4f),
                color = DarkPurple,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Qty",
                modifier = Modifier.weight(0.2f),
                color = DarkPurple,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unit",
                modifier = Modifier.weight(0.2f),
                color = DarkPurple,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(0.2f))
        }
        IngredientTable(
            ingredients = recipe.ingredients,
            checkboxStates = checkboxStates,
            onCheckedChange = { index, isChecked ->
                checkboxStates[index] = isChecked
            },
            onIngredientNameClick = { ingredient ->
                selectedIngredient = ingredient
                showSubstituteIngredientsDialog = true
                onSubstituteIngredients(ingredient.name)
            },
            enableButtons = enableButtons
        )
        if (showSubstituteIngredientsDialog && selectedIngredient != null) {
            SubstituteIngredientsAlertDialog(
                substituteIngredientsState = substituteIngredientsState,
                onDismiss = { showSubstituteIngredientsDialog = false },
                enableButtons = enableButtons
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onConfirmIngredients,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            enabled = !checkboxStates.contains(false) && enableButtons
        ) { Text("Confirm Ingredients") }
    }
}