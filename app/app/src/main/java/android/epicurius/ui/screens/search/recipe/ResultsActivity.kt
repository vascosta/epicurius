package android.epicurius.ui.screens.search.recipe

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.search.general.SearchActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dummyRecipes = List(10) { index ->
            RecipeInfo(
                id = index,
                name = "Recipe $index",
                authorUsername = "Author $index",
                rating = 3.5,
                cuisine = Cuisine.MEDITERRANEAN,
                mealType = MealType.MAIN_COURSE,
                preparationTime = 30,
                servings = 4,
                picture = ByteArray(0)
            )
        }
        setContent {
            ResultsScreen(
                recipeList = dummyRecipes,
                onBackButton = { navigateTo<SearchActivity>() },
                onRecipeRequest = { navigateTo<RecipeProfileActivity>() }
            )
        }
    }
}