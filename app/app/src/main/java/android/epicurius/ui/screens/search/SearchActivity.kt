package android.epicurius.ui.screens.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.search.camera.CameraActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState

class SearchActivity : EpicuriusActivity() {
    override val viewModel: SearchViewModel by getViewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val recipesResultState = viewModel.searchedRecipes.collectAsState(idle())
            val usersResultState = viewModel.searchedUsers.collectAsState(idle())
            SearchScreen(
                recipesResultState = recipesResultState.value,
                usersResultState = usersResultState.value,
                onBackButton = { finish() },
                onSearchRecipes = {
                    name: String?,
                    cuisine: List<Cuisine>?,
                    mealType: List<MealType>?,
                    ingredients: List<Ingredient>?,
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
                    maxTime: Int? ->
                    viewModel.searchRecipes(
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
                        maxTime
                    )
                },
                onSearchUsers = { name: String -> viewModel.searchUsers(name) },
                onSearchRecipesClear = { viewModel.clearSearchRecipes() },
                onSearchUsersClear = { viewModel.clearSearchUsers() },
                onCamera = { navigateTo<CameraActivity>() },
                onIdentifyIngredientsInPicture = {},
                onConfirm = {},
                onUserProfileRequest = ::navigateToUserProfileActivity,
                onRecipeProfileRequest = ::navigateToRecipeProfileActivity,
                enableButtons = viewModel.enableButtons,
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