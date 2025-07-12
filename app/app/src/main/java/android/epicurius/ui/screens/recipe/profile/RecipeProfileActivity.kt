package android.epicurius.ui.screens.recipe.profile

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.recipeCollections.RecipeCollectionsViewModel
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.createRecipe.CreateRecipeActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RecipeProfileActivity : EpicuriusActivity() {
    override val viewModel: RecipeProfileViewModel by getViewModel<RecipeProfileViewModel>()
    val recipeCollectionsViewModel: RecipeCollectionsViewModel by getViewModel<RecipeCollectionsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.recipe.collectLatest { recipeState ->
                if (recipeState is Idle) {
                    val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1) // never reaches -1
                    viewModel.getRecipeProfile(recipeId) { finish() }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.username.collectLatest { usernameState ->
                if (usernameState is Idle) viewModel.getUsername()
            }
        }
        lifecycleScope.launch {
            viewModel.userRecipeRating.collectLatest { userRatingState ->
                val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1) // never reaches -1
                if (userRatingState is Idle) viewModel.getUserRecipeRating(recipeId) { finish() }
            }
        }
        setContent {
            val recipeState = viewModel.recipe.collectAsState(Idle)
            val recipeNameState = viewModel.recipeName.collectAsState(Idle)
            val usernameState = viewModel.username.collectAsState(Idle)
            val userRecipeRatingState = viewModel.userRecipeRating.collectAsState(Idle)
            val collectionsToAddRecipeState = recipeCollectionsViewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = recipeCollectionsViewModel.collectionsToRemoveRecipe.collectAsState(idle())
            val substituteIngredientsState = viewModel.substituteIngredients.collectAsState(idle())
            val ingredientsResultState = viewModel.searchedIngredients.collectAsState(idle())
            RecipeProfileScreen(
                recipeState = recipeState.value,
                recipeNameState = recipeNameState.value,
                usernameState = usernameState.value,
                userRecipeRatingState = userRecipeRatingState.value,
                recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                    collectionsToAddRecipeState = collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState = collectionsToRemoveRecipeState.value
                ),

                ingredientsResultState = ingredientsResultState.value,
                substituteIngredientsState = substituteIngredientsState.value,
                backButton = intent.getStringExtra(Intents.SOURCE_ACTIVITY) != CreateRecipeActivity::class.java.name,
                onBackButton = { finish() },
                onEditRecipe = {
                    name: String?,
                    description: String?,
                    servings: Int?,
                    preparationTime: Int?,
                    cuisine: Cuisine?,
                    mealType: MealType?,
                    intolerances: Set<Intolerance>?,
                    diets: Set<Diet>?,
                    ingredients: List<Ingredient>?,
                    calories: Int?,
                    protein: Int?,
                    fat: Int?,
                    carbs: Int?,
                    instructions: Instructions?
                    ->
                    viewModel.updateRecipe(
                        intent.getIntExtra(Intents.RECIPE_ID, -1), // never reaches -1
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
                        instructions
                    )
                },
                onEditRecipePictures = { picturesBytes: List<ByteArray> ->
                    viewModel.updateRecipePictures(
                        intent.getIntExtra(Intents.RECIPE_ID, -1), // never reaches -1
                        picturesBytes
                    )
                },
                onEditUserRating = { rating: Int ->
                    viewModel.updateUserRecipeRating(
                        intent.getIntExtra(Intents.RECIPE_ID, -1), // never reaches -1
                        rating
                    )
                },
                onDeleteUserRecipeRating = { recipeId: Int ->
                    viewModel.deleteUserRecipeRating(recipeId)
                },
                onDeleteRecipe = {
                    viewModel.deleteRecipe(intent.getIntExtra(Intents.RECIPE_ID, -1)) { finish() } // never reaches -1
                },
                onAddRecipeToCollections = {
                    recipeId: Int,
                    collectionsToAdd: List<CollectionProfile>
                    ->
                    recipeCollectionsViewModel.addRecipeToCollections(
                        recipeId,
                        collectionsToAdd
                    )
                },
                onRemoveRecipeFromCollections = {
                    recipeId: Int,
                    collectionsToRemove: List<CollectionProfile>,
                    ->
                    recipeCollectionsViewModel.removeRecipeFromCollections(
                        recipeId,
                        collectionsToRemove
                    )
                },
                onSearchIngredients = { partialName -> viewModel.searchIngredients(partialName) },
                onSubstituteIngredients = { ingredientName: String ->
                    viewModel.getSubstituteIngredients(ingredientName)
                },
                onRateRecipe = { rating ->
                    viewModel.rateRecipe(intent.getIntExtra(Intents.RECIPE_ID, -1), rating) // never reaches -1
                },
                onRecipeCollectionsClear = { recipeCollectionsViewModel.clearRecipeCollections() },
                onUserProfileRequest = ::navigateToUserProfileActivity,
                onRecipeCollectionsRequest = { recipeId: Int, collectionType: CollectionType ->
                    recipeCollectionsViewModel.getRecipeCollections(recipeId, collectionType)
                },
                enableButtons = viewModel.enableButtons,
            )
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<UserProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}
