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
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.recipe.collectLatest { recipeState ->
                if (recipeState is Idle) {
                    val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1)
                    viewModel.getRecipeProfile(recipeId) { finish() }
                }
            }
        }
        lifecycleScope.launch {
            combine (
                viewModel.username,
                viewModel.userRecipeRating
            ) { usernameState, userRatingState,  -> usernameState to userRatingState }
            .collectLatest { (usernameState, userRatingState) ->
                val recipeId = intent.getIntExtra(Intents.RECIPE_ID, -1)
                if (usernameState is Idle) viewModel.getUsername()
                if (userRatingState is Idle) viewModel.getUserRecipeRating(recipeId) { finish() }
            }
        }
        setContent {
            val recipeState = viewModel.recipe.collectAsState(Idle)
            val recipeNameState = viewModel.recipeName.collectAsState(Idle)
            val usernameState = viewModel.username.collectAsState(Idle)
            val userRecipeRatingState = viewModel.userRecipeRating.collectAsState(Idle)
            val collectionsToAddRecipeState = viewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = viewModel.collectionsToRemoveRecipe.collectAsState(idle())
            val substituteIngredientsState = viewModel.substituteIngredients.collectAsState(idle())
            RecipeProfileScreen(
                recipeState = recipeState.value,
                recipeNameState = recipeNameState.value,
                usernameState = usernameState.value,
                userRecipeRatingState = userRecipeRatingState.value,
                recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                    collectionsToAddRecipeState = collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState = collectionsToRemoveRecipeState.value
                ),
                substituteIngredientsState = substituteIngredientsState.value,
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
                        intent.getIntExtra(Intents.RECIPE_ID, -1),
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
                        intent.getIntExtra(Intents.RECIPE_ID, -1),
                        picturesBytes
                    )
                },
                onEditUserRating = { rating: Int ->
                    viewModel.updateUserRecipeRating(
                        intent.getIntExtra(Intents.RECIPE_ID, -1),
                        rating
                    )
                },
                onDeleteUserRecipeRating = {
                    viewModel.deleteUserRecipeRating(intent.getIntExtra(Intents.RECIPE_ID, -1))
                },
                onDeleteRecipe = {
                    viewModel.deleteRecipe(intent.getIntExtra(Intents.RECIPE_ID, -1)) { finish() }
                },
                onAddRecipeToCollections = {
                    recipeId: Int,
                    collectionsToAdd: List<CollectionProfile>
                    ->
                    viewModel.addRecipeToCollections(
                        recipeId,
                        collectionsToAdd
                    )
                },
                onRemoveRecipeFromCollections = {
                    recipeId: Int,
                    collectionsToRemove: List<CollectionProfile>,
                    ->
                    viewModel.removeRecipeFromCollections(
                        recipeId,
                        collectionsToRemove
                    )
                },
                onSubstituteIngredients = { ingredientName: String ->
                    viewModel.getSubstituteIngredients(ingredientName)
                },
                onRecipeCollectionsClear = { viewModel.clearRecipeCollections() },
                onUserProfileRequest = ::navigateToUserProfileActivity,
                onRecipeCollectionsRequest = { recipeId: Int, collectionType: CollectionType ->
                    viewModel.getRecipeCollections(recipeId, collectionType)
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
