package android.epicurius.ui.screens.favourites.list

import android.epicurius.MainActivity
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class FavouritesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavouritesListScreen(
                onBackButton = { navigateTo<MainActivity>() },
                folderName = "My Favourite Recipes",
                recipeList = listOf(
                    RecipeInfo(
                        id = 1,
                        name = "Spaghetti Carbonara",
                        authorUsername = "ChefBear",
                        cuisine = Cuisine.ITALIAN,
                        mealType = MealType.MAIN_COURSE,
                        preparationTime = 35,
                        servings = 4,
                        picture = "".toByteArray()
                    )
                )
            )
        }
    }
}