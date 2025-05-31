package android.epicurius.ui.screens.recipe.ingredients

import android.epicurius.MainActivity
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ConfirmIngredientsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfirmIngredientsScreen(
                onBackButton = { navigateTo<MainActivity>() },
                recipeName = "Chocolate Cake",
                ingredientsList = listOf(
                    Ingredient("Flour", 2.0, IngredientUnit.COFFEE_CUP),
                    Ingredient("Sugar", 1.5, IngredientUnit.COFFEE_CUP),
                    Ingredient("Eggs", 3.0, IngredientUnit.X)
                )
            )
        }
    }
}