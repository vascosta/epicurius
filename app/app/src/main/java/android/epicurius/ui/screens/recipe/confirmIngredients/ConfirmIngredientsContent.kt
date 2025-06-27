package android.epicurius.ui.screens.recipe.confirmIngredients

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.confirmIngredients.components.InfoDialog
import android.epicurius.ui.screens.recipe.confirmIngredients.components.IngredientTable
import android.epicurius.ui.screens.recipe.confirmIngredients.components.SubstituteIngredientsAlertDialog
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
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
import androidx.compose.runtime.LaunchedEffect
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
    recipeState: LoadState<Recipe>,
    substituteIngredientsState: LoadState<List<String>>,
    onSubstituteIngredients: (ingredientName: String) -> Unit = {},
    onConfirmIngredients: () -> Unit = {},
    enableButtons: Boolean,
    paddingValues: PaddingValues
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showSubstituteIngredientsDialog by remember { mutableStateOf(false) }

    val checkboxStates = remember { mutableStateListOf<Boolean>() }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }

    LaunchedEffect(recipeState) {
        if (recipeState is Loaded) {
            val ingredients = recipeState.value.getValueOrThrow().ingredients
            checkboxStates.clear()
            repeat(ingredients.size) { checkboxStates.add(false) }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadStateRenderer(
            loadState = recipeState,
            content = { recipe ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ingredients:",
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
                if (showInfoDialog) InfoDialog { showInfoDialog = false }
                Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)) {
                    Text("Name", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold)
                    Text("Qty", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
                    Text("Unit", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
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
                    LoadStateRenderer(
                        loadState = substituteIngredientsState,
                        content = { substituteIngredients ->
                            SubstituteIngredientsAlertDialog(
                                substituteIngredients = substituteIngredients,
                                onDismiss = { showSubstituteIngredientsDialog = false }
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onConfirmIngredients,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    enabled = !checkboxStates.contains(false) && enableButtons
                ) { Text("Confirm Ingredients") }
            }
        )
    }
}