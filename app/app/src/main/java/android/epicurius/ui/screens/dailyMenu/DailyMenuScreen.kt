package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.recipe.RecipeInfoSimpleBox
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyMenuScreen(menu: Map<String, RecipeInfo?>) {
    Scaffold(
        topBar = { TopBar("Today's Menu") },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuItemBox("Breakfast", menu["Breakfast"])
                MenuItemBox("Soup", menu["Soup"])
                MenuItemBox("Lunch", menu["Lunch"])
                MenuItemBox("Dinner", menu["Dinner"])
                MenuItemBox("Dessert", menu["Dessert"])
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun MenuItemBox(title: String, recipe: RecipeInfo?) {
    Box(modifier = Modifier.padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            if (recipe != null) {
                RecipeInfoSimpleBox(recipe)
            } else {
                Text(
                    text = "$title recipe is not available today",
                    modifier = Modifier.padding(10.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun DailyMenuPreview() {
    val menu = mapOf(
        "Breakfast" to RecipeInfo(
            id = 1,
            name = "Pancakes",
            cuisine = Cuisine.AMERICAN,
            mealType = MealType.BREAKFAST,
            preparationTime = 20,
            servings = 2,
            picture = "".toByteArray()
        ),
        "Lunch" to RecipeInfo(
            id = 2,
            name = "Caesar Salad",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 15,
            servings = 1,
            picture = "".toByteArray()
        )
    )
    DailyMenuScreen(menu)
}
