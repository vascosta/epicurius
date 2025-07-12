package android.epicurius.ui.screens.recipe.createRecipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState

class CreateRecipeActivity : EpicuriusActivity() {
    override val viewModel: CreateRecipeViewModel by getViewModel<CreateRecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ingredientsResultState = viewModel.searchedIngredients.collectAsState(idle())
            CreateRecipeScreen(
                ingredientsResultState = ingredientsResultState.value,
                onCreateRecipe = {
                    name: String,
                    description: String,
                    servings: Int,
                    preparationTime: Int,
                    cuisine: Cuisine,
                    mealType: MealType,
                    intolerances: Set<Intolerance>,
                    diets: Set<Diet>,
                    ingredients: List<Ingredient>,
                    calories: Int?,
                    protein: Int?,
                    fat: Int?,
                    carbs: Int?,
                    instructions: Instructions,
                    picturesBytes: List<ByteArray>
                    ->
                    viewModel.createRecipe(
                        name,
                        description,
                        servings,
                        preparationTime,
                        cuisine,
                        mealType,
                        intolerances,
                        diets,
                        ingredients,
                        calories,
                        protein,
                        fat,
                        carbs,
                        instructions,
                        picturesBytes,
                        ::navigateToRecipeProfileActivity
                    )
                },
                onSearchIngredients = { partialName -> viewModel.searchIngredients(partialName) },
                buttonsEnable = viewModel.enableButtons
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity>(finishCurrent = true) { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
            intent.putExtra(Intents.SOURCE_ACTIVITY, CreateRecipeActivity::class.java.name)
        }
    }
}
