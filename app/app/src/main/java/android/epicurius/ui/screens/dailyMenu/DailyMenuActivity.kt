package android.epicurius.ui.screens.dailyMenu

import android.epicurius.MainActivity
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class DailyMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyMenuScreen(
                onBackButton = { navigateTo<MainActivity>() },
                menu = mapOf(
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
            )
        }
    }
}