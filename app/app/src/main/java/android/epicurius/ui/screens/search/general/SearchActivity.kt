package android.epicurius.ui.screens.search.general

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.search.camera.CameraActivity
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState

class SearchActivity : EpicuriusActivity() {
    override val viewModel: SearchViewModel by getViewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val usersResultState = viewModel.searchedUsers.collectAsState(idle())
            SearchScreen(
                usersResultState = usersResultState.value,
                onBackButton = { finish() },
                onRecipeSearch = { _, _, _,_,_,_, _, _, _, _, _, _, _, _, _ ->
                    listOf(
                        RecipeInfo(
                            id = 1,
                            name = "Spaghetti Carbonara",
                            authorUsername = "ChefBear",
                            rating = 4.5,
                            cuisine = Cuisine.ITALIAN,
                            mealType = MealType.MAIN_COURSE,
                            preparationTime = 30,
                            servings = 4,
                            picture = "",
                            isInCollection = true
                        ),
                        RecipeInfo(
                            id = 2,
                            name = "Caesar Salad",
                            authorUsername = "ChefBear",
                            rating = 4.3,
                            cuisine = Cuisine.ITALIAN,
                            mealType = MealType.SALAD,
                            preparationTime = 15,
                            servings = 2,
                            picture = "",
                            isInCollection = false
                        )
                    )
                },
                onSearchUsers = { name: String -> viewModel.searchUsers(name) },
                onSearchUsersClear = { viewModel.clearSearchUsers() },
                onCamera = { navigateTo<CameraActivity>() },
                onIdentifyIngredientsInPicture = {},
                onConfirm = {},
                onLoadMoreSearchedUsers = { name: String -> viewModel.searchUsers(name) },
                onUserProfileRequest = ::navigateToUserProfileActivity,
                enableButtons = viewModel.enableButtons,
            )
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}