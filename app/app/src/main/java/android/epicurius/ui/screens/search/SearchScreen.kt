package android.epicurius.ui.screens.search

import android.Manifest
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.search.camera.CameraScreen
import android.epicurius.ui.screens.search.components.ConfirmIngredientsDialog
import android.epicurius.ui.screens.search.components.FiltersDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.search.components.SearchPhotoComponent
import android.epicurius.ui.screens.search.components.SearchScreenContent
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    recipesResultState: LoadState<List<RecipeInfo>>,
    usersResultState: LoadState<List<SearchUser>>,
    ingredientsState: LoadState<List<String>>,
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
    onSearchRecipesClear: () -> Unit,
    onSearchUsersClear: () -> Unit,
    onIngredientsClear: () -> Unit,
    onIdentifyIngredientsInPicture: (ByteArray) -> Unit,
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
            onIngredientsClear = {
                ingredients = emptyList()
                onIngredientsClear()
            },
            onConfirmIngredients = { ingredientsList ->
                ingredients += ingredientsList
                showCameraScreen = false
            },
            enableButtons = enableButtons
        )
    } else {
        SearchScreenContent(
            recipesResultState = recipesResultState,
            usersResultState = usersResultState,
            ingredientsState = ingredientsState,
            ingredientsList = ingredients,
            onBackButton = onBackButton,
            onSearchRecipes = onSearchRecipes,
            onSearchUsers = onSearchUsers,
            onCamera = { showCameraScreen = true },
            onSearchRecipesClear = onSearchRecipesClear,
            onSearchUsersClear = onSearchUsersClear,
            onIngredientsClear = {
                ingredients = emptyList()
                onIngredientsClear()
            },
            onIdentifyIngredientsInPicture = onIdentifyIngredientsInPicture,
            onUserProfileRequest = onUserProfileRequest,
            onRecipeProfileRequest = onRecipeProfileRequest,
            onConfirmIngredients = { ingredientsList -> ingredients += ingredientsList },
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

    SearchScreen(
        recipesResultState = Idle,
        usersResultState = apiSuccess(emptyList()),
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
