package android.epicurius.ui.screens.feed

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class FeedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedScreen(
                onBackButton = { finish() },
                recipeList = listOf(
                    RecipeInfo(
                        id = 1,
                        name = "Spaghetti Bolognese",
                        authorUsername = "ChefBear",
                        cuisine = Cuisine.ITALIAN,
                        mealType = MealType.MAIN_COURSE,
                        preparationTime = 30,
                        servings = 4,
                        picture = "".toByteArray()
                    ),
                    RecipeInfo(
                        id = 2,
                        name = "Chicken Curry",
                        authorUsername = "ChefBear",
                        cuisine = Cuisine.INDIAN,
                        mealType = MealType.MAIN_COURSE,
                        preparationTime = 45,
                        servings = 4,
                        picture = "".toByteArray()
                    )
                )
            )
        }
    }
}
