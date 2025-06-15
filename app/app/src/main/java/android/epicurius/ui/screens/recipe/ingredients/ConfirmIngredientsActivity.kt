package android.epicurius.ui.screens.recipe.ingredients

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.preparation.PreparationActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ConfirmIngredientsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfirmIngredientsScreen(
                recipeName = "Chocolate Cake",
                ingredientsList = listOf(
                    Ingredient("Flour", 2.0, IngredientUnit.COFFEE_CUP),
                    Ingredient("Sugar", 1.5, IngredientUnit.COFFEE_CUP),
                    Ingredient("Eggs", 3.0, IngredientUnit.X)
                ),
                onBackButton = { navigateTo<RecipeProfileActivity>() },
                onSubstituteIngredients = { ingredientName ->
                    listOf("Substitute for $ingredientName", "Another substitute for $ingredientName")
                },
                onConfirmIngredients = { navigateTo<PreparationActivity>() }
            )
        }
    }
}