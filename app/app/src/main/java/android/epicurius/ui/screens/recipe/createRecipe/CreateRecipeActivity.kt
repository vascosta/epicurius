package android.epicurius.ui.screens.recipe.createRecipe

import android.epicurius.ui.EpicuriusActivity
import android.os.Bundle
import androidx.activity.compose.setContent

class CreateRecipeActivity : EpicuriusActivity() {
    override val viewModel: CreateRecipeViewModel by getViewModel<CreateRecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateRecipeScreen(
                onBackButton = { finish() },
                onCreateRecipe = {},
                onPublish = TODO(),
                buttonsEnable = viewModel.buttonsEnable
            )
        }
    }
}
