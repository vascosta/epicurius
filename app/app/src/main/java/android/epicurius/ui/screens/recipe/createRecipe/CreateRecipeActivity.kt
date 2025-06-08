package android.epicurius.ui.screens.recipe.createRecipe

import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CreateRecipeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateRecipeScreen(
                onBackButton = {  },
            )
        }
    }
}
