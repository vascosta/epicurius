package android.epicurius.ui.screens.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.recipeCollections.RecipeCollectionsViewModel
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : EpicuriusActivity() {
    override val viewModel: SearchUsersViewModel by getViewModel<SearchUsersViewModel>()
    val searchRecipesViewModel: SearchRecipesViewModel by getViewModel<SearchRecipesViewModel>()
    val recipeCollectionsViewModel: RecipeCollectionsViewModel by getViewModel<RecipeCollectionsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userInfo.collectLatest { state ->
                if (state is Idle) viewModel.getUserInfo()
            }
        }
        setContent {
            val recipesResultState = searchRecipesViewModel.searchedRecipes.collectAsState(idle())
            val usersResultState = viewModel.searchedUsers.collectAsState(idle())
            val ingredientsState = searchRecipesViewModel.ingredients.collectAsState(idle())
            val userInfoState = viewModel.userInfo.collectAsState(idle())
            val collectionsToAddRecipeState = recipeCollectionsViewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = recipeCollectionsViewModel.collectionsToRemoveRecipe.collectAsState(idle())
            SearchScreen(
                recipesResultState = recipesResultState.value,
                usersResultState = usersResultState.value,
                ingredientsState = ingredientsState.value,
                userInfoState = userInfoState.value,
                recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                    collectionsToAddRecipeState.value,
                    collectionsToRemoveRecipeState.value
                ),
                onBackButton = { finish() },
                onSearchRecipes = {
                    name: String?,
                    cuisine: List<Cuisine>?,
                    mealType: List<MealType>?,
                    ingredients: Set<String>?,
                    intolerances: List<Intolerance>?,
                    diets: List<Diet>?,
                    servings: Int?,
                    minCalories: Int?,
                    maxCalories: Int?,
                    minCarbs: Int?,
                    maxCarbs: Int?,
                    minFat: Int?,
                    maxFat: Int?,
                    minProtein: Int?,
                    maxProtein: Int?,
                    minTime: Int?,
                    maxTime: Int?,
                    showAuthorRecipes: Boolean
                    ->
                    searchRecipesViewModel.searchRecipes(
                        name,
                        cuisine,
                        mealType,
                        ingredients,
                        intolerances,
                        diets,
                        servings,
                        minCalories,
                        maxCalories,
                        minCarbs,
                        maxCarbs,
                        minFat,
                        maxFat,
                        minProtein,
                        maxProtein,
                        minTime,
                        maxTime,
                        showAuthorRecipes
                    )
                },
                onSearchUsers = { name: String -> viewModel.searchUsers(name) },
                onIdentifyIngredientsInPicture = { pictureBytes: ByteArray ->
                    searchRecipesViewModel.identifyIngredientsInPicture(pictureBytes)
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
                onSearchRecipesClear = { searchRecipesViewModel.clearSearchRecipes() },
                onSearchUsersClear = { viewModel.clearSearchUsers() },
                onIngredientsClear = { searchRecipesViewModel.clearIngredients() },
                onRecipeCollectionsClear = { recipeCollectionsViewModel.clearRecipeCollections() },
                onRecipeProfileRequest = ::navigateToRecipeProfileActivity,
                onUserProfileRequest = ::navigateToUserProfileActivity,
                onRecipeCollectionsRequest = { recipeId: Int ->
                    recipeCollectionsViewModel.getRecipeCollections(recipeId, CollectionType.FAVOURITE)
                },
                enableButtons = viewModel.enableButtons && searchRecipesViewModel.enableButtons
            )
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<UserProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}