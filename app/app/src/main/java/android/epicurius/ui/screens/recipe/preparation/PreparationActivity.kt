package android.epicurius.ui.screens.recipe.preparation

import android.epicurius.domain.recipe.Instructions
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class PreparationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreparationScreen(
                recipeName = "Chocolate Cake",
                instructions =  Instructions(
                    steps = mapOf(
                        "1" to "Preheat the oven to 180°C.",
                        "2" to "Mix all ingredients in a bowl.",
                        "3" to "Pour the mixture into a baking dish.",
                        "4" to "Bake for 30 minutes or until golden brown."
                    )
                ),
                onBackButton = { navigateTo<RecipeProfileActivity>() },
                onRateRecipe = { navigateTo<RecipeProfileActivity>() },
                onSkipRating = { navigateTo<RecipeProfileActivity>() },
                onCancelPreparation = { navigateTo<RecipeProfileActivity>() }
            )
        }
    }
}
