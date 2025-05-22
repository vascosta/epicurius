package android.epicurius.ui.screens.feed

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.recipe.RecipeInfoBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FeedScreen(recipeList: List<RecipeInfo>) {
    Scaffold(
        topBar = { TopBar(text = "For you to cook") },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                recipeList.forEach { recipe ->
                    RecipeInfoBox(recipe)
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun FeedPreview() {
    val recipeList = listOf(
        RecipeInfo(
            id = 1,
            name = "Spaghetti Bolognese",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = "".toByteArray()
        ),
        RecipeInfo(
            id = 2,
            name = "Chicken Curry",
            cuisine = Cuisine.INDIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 45,
            servings = 4,
            picture = "".toByteArray()
        )
    )

    FeedScreen(recipeList)
}