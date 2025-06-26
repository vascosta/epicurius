package android.epicurius.ui.screens.recipe.ingredients

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.ingredients.components.InfoDialog
import android.epicurius.ui.screens.recipe.ingredients.components.IngredientTable
import android.epicurius.ui.screens.recipe.ingredients.components.SubstituteIngredientsAlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmIngredientsScreen(
    recipeName: String,
    ingredientsList: List<Ingredient>,
    onBackButton: () -> Unit,
    onSubstituteIngredients: (String) -> List<String>,
    onConfirmIngredients: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                titleText = recipeName,
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = true
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {

            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun ConfirmIngredientsActivityPreview() {
    val recipeName = "Chocolate Cake"
    val ingredients = listOf(
        Ingredient("Flour", 2.0, IngredientUnit.COFFEE_CUP),
        Ingredient("Sugar", 1.5, IngredientUnit.COFFEE_CUP),
        Ingredient("Eggs", 3.0, IngredientUnit.X)
    )
    val substituteIngredients = { ingredientName: String ->
        listOf("Substitute for $ingredientName", "Another substitute for $ingredientName")
    }
    ConfirmIngredientsScreen(
        recipeName = recipeName,
        ingredientsList = ingredients,
        onBackButton = {},
        onSubstituteIngredients = { ingredientName -> substituteIngredients(ingredientName) },
        onConfirmIngredients = {}
    )
}
