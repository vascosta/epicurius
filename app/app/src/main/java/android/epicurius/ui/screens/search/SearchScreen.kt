package android.epicurius.ui.screens.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.Picture
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.screens.search.camera.CameraScreen
import android.epicurius.ui.screens.search.components.SearchScreenContent
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    recipesResultState: LoadState<List<RecipeInfo>>,
    usersResultState: LoadState<List<SearchUser>>,
    ingredientsState: LoadState<List<String>>,
    userInfoState: LoadState<UserInfo>,
    onBackButton: () -> Unit,
    onSearchRecipes: (
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<String>?,
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
        maxTime: Int?
    ) -> Unit,
    onSearchUsers: (name: String) -> Unit,
    onIdentifyIngredientsInPicture: (pictureBytes: ByteArray) -> Unit,
    onSearchRecipesClear: () -> Unit,
    onSearchUsersClear: () -> Unit,
    onIngredientsClear: () -> Unit,
    onUserProfileRequest: (name: String) -> Unit,
    onRecipeProfileRequest: (recipeId: Int) -> Unit,
    enableButtons: Boolean
) {
    var showCameraScreen by remember { mutableStateOf(false) }

    var ingredients by remember { mutableStateOf(listOf<String>()) }

    if (showCameraScreen) {
        CameraScreen(
            ingredientsState = ingredientsState,
            onBackButton = { showCameraScreen = false },
            onIdentifyIngredients = onIdentifyIngredientsInPicture,
            onConfirmIngredients = { ingredientsList ->
                ingredients += ingredientsList
                showCameraScreen = false
            },
            onIngredientsClear = {
                ingredients = emptyList()
                onIngredientsClear()
            },
            enableButtons = enableButtons
        )
    } else {
        SearchScreenContent(
            recipesResultState = recipesResultState,
            usersResultState = usersResultState,
            ingredientsState = ingredientsState,
            userInfoState = userInfoState,
            ingredientsList = ingredients,
            onBackButton = onBackButton,
            onSearchRecipes = onSearchRecipes,
            onSearchUsers = onSearchUsers,
            onIdentifyIngredientsInPicture = onIdentifyIngredientsInPicture,
            onConfirmIngredients = { ingredientsList -> ingredients += ingredientsList },
            onCamera = { showCameraScreen = true },
            onSearchRecipesClear = onSearchRecipesClear,
            onSearchUsersClear = onSearchUsersClear,
            onIngredientsClear = {
                ingredients = emptyList()
                onIngredientsClear()
            },
            onUserProfileRequest = onUserProfileRequest,
            onRecipeProfileRequest = onRecipeProfileRequest,
            enableButtons = enableButtons
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val recipeList = listOf(
        RecipeInfo(
            id = 1,
            name = "Spaghetti Carbonara",
            authorUsername = "ChefBear",
            rating = 4.5,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = ""
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
            picture = ""
        )
    )

    SearchScreen(
        recipesResultState = Idle,
        usersResultState = apiSuccess(emptyList()),
        userInfoState = apiSuccess(
            UserInfo(
                name = "ChefBear",
                email = "email",
                country = "PT",
                privacy = true,
                intolerances = listOf(Intolerance.GLUTEN, Intolerance.DAIRY),
                diets = listOf(Diet.GLUTEN_FREE),
                profilePictureName = "",
            )
        ),
        ingredientsState = Idle,
        onBackButton = {},
        onSearchRecipes = { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
        onSearchUsers = {},
        onSearchRecipesClear = {},
        onSearchUsersClear = {},
        onIngredientsClear = {},
        onIdentifyIngredientsInPicture = {},
        onUserProfileRequest = {},
        onRecipeProfileRequest = {},
        enableButtons = true,
    )
}
