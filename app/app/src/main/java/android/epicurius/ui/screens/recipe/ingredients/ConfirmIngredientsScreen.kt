package android.epicurius.ui.screens.recipe.ingredients

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmIngredientsActivity(recipeName: String, ingredientsList: List<Ingredient>) {
    val checkboxStates = remember { mutableStateListOf<Boolean>().apply {
        repeat(ingredientsList.size) { add(false) }
    }}
    val showDialog = remember { mutableStateOf(false) }
    val selectedIngredient = remember { mutableStateOf<Ingredient?>(null) }

    Scaffold(
        topBar = { TopBar(recipeName, backButton = true) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Ingredients:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)) {
                    Text("Name", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold)
                    Text("Qty", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
                    Text("Unit", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(0.2f))
                }

                IngredientTable(
                    ingredients = ingredientsList,
                    checkboxStates = checkboxStates,
                    onCheckedChange = { index, isChecked ->
                        checkboxStates[index] = isChecked
                    },
                    onNameClick = { ingredient ->
                        selectedIngredient.value = ingredient
                        showDialog.value = true
                    }
                )

                if (showDialog.value && selectedIngredient.value != null) {
                    selectedIngredient.value?.let { observedIngredient ->
                        SubstituteIngredientsAlertDialog(
                            ingredient = observedIngredient,
                            onDismiss = { showDialog.value = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp, bottom = 16.dp),
                    enabled = !checkboxStates.contains(false)
                ) {
                    Text("Confirm Ingredients")
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun IngredientTable(
    ingredients: List<Ingredient>,
    checkboxStates: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    onNameClick: (Ingredient) -> Unit
) {
    ingredients.forEachIndexed { index, ingredient ->
        IngredientBulletPoint(
            ingredient = ingredient,
            checked = checkboxStates[index],
            onCheckedChange = { isChecked -> onCheckedChange(index, isChecked) },
            onNameClick = { onNameClick(ingredient) }
        )
    }
}


@Composable
private fun IngredientBulletPoint(
    ingredient: Ingredient,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onNameClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.name,
            modifier = Modifier
                .weight(0.4f)
                .clickable { onNameClick() },
            fontSize = 18.sp
        )

        Text(
            text = if (ingredient.quantity % 1.0 == 0.0)
                ingredient.quantity.toInt().toString()
            else
                ingredient.quantity.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 18.sp
        )

        Text(
            text = if (ingredient.unit == IngredientUnit.X) "" else ingredient.unit.displayName,
            modifier = Modifier.weight(0.2f),
            fontSize = 18.sp
        )

        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.weight(0.1f)
        )

        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Shopping List",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.1f)
        )
    }
}

@Composable
private fun SubstituteIngredientsAlertDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Substitute Ingredients") },
        text = {
            val substitutes = listOf(ingredient.name) // Replace with actual substitute logic
            substitutes.forEach {
                Text(text = "• $it", modifier = Modifier.padding(start = 10.dp, bottom = 10.dp))
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
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
    ConfirmIngredientsActivity(recipeName, ingredientsList = ingredients)
}
