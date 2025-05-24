package android.epicurius.ui.screens.recipe.addRecipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.recipe.addRecipe.utils.IngredientsComponent
import android.epicurius.ui.screens.recipe.addRecipe.utils.InstructionsComponent
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.FormTextField
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class IngredientComponent(
    val name: String = "",
    val quantity: String = "",
    val unit: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen() {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var serving by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf("") }
    var intolerances by remember { mutableStateOf(listOf<String>()) }
    var diets by remember { mutableStateOf(listOf<String>()) }
    var ingredients by remember { mutableStateOf(listOf<IngredientComponent>()) }
    var instructions by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = { TopBar("Create recipe") },
        bottomBar = { BottomBar() },
        containerColor = Color.White,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recipe Form",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                FormTextField("Name", name, Modifier.height(56.dp)) { name = it }
                FormTextField("Description", description, Modifier.height(56.dp)) { description = it }

                NumberTextField("Duration (min)", duration) { duration = it }
                NumberTextField("Serving (px)", serving) { serving = it }

                DropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    value = mealType,
                    onValueChange = { mealType = it },
                    label = "Meal Type",
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                DropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    label = "Cuisine",
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = { intolerances = it },
                    label = "Intolerances",
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = { diets = it },
                    label = "Diets",
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                DividerComponent()

                IngredientsComponent(ingredients) { ingredients = it }

                DividerComponent()

                InstructionsComponent(steps = instructions) { instructions = it }

                DividerComponent()

                Button(
                    onClick = { },
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text("Upload")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Image, contentDescription = null)
                }

                Button(
                    onClick = { },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Publish")
                }
            }
        }
    )
}

@Composable
fun DividerComponent() {
    HorizontalDivider(
        modifier = Modifier.padding(
            top = 15.dp,
            bottom = 10.dp,
            start = 15.dp,
            end = 15.dp
        ),
        color = Color.Black,
        thickness = 1.dp
    )
}

@Preview
@Composable
fun AddRecipeScreenPreview() {
    AddRecipeScreen()
}