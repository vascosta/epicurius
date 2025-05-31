package android.epicurius.ui.screens.recipe.preparation

import android.epicurius.MainActivity
import android.epicurius.domain.recipe.Instructions
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class PreparationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreparationScreen(
                onBackButton = { navigateTo<MainActivity>() },
                recipeName = "Chocolate Cake",
                instructions =  Instructions(
                    steps = mapOf(
                        "1" to "Preheat the oven to 180°C.",
                        "2" to "Mix all ingredients in a bowl.",
                        "3" to "Pour the mixture into a baking dish.",
                        "4" to "Bake for 30 minutes or until golden brown."
                    )
                )
            )
        }
    }
}
