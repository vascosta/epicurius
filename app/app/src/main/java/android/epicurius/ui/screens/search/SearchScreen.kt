package android.epicurius.ui.screens.search

import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.utils.generateTestImageByteArray
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
import java.util.Base64

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    recipesResultState: LoadState<List<RecipeInfo>>,
    usersResultState: LoadState<List<SearchUser>>,
    ingredientsState: LoadState<List<String>>,
    userInfoState: LoadState<UserInfo>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onSearchRecipes: (
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
        showAuthorRecipes: Boolean,
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onSearchUsers: (name: String) -> Unit = {},
    onIdentifyIngredientsInPicture: (pictureBytes: ByteArray) -> Unit = {},
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onSearchRecipesClear: () -> Unit = {},
    onSearchUsersClear: () -> Unit = {},
    onIngredientsClear: () -> Unit = {},
    onRecipeCollectionsClear: () -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onRecipeProfileRequest: (recipeId: Int) -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int, isRecipeAuthor: Boolean) -> Unit = { _, _ -> },
    enableButtons: Boolean
) {
    var showCameraScreen by remember { mutableStateOf(false) }

    var ingredients by remember { mutableStateOf(setOf<String>()) }

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
                ingredients = emptySet()
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
            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
            ingredientsList = ingredients,
            onSearchRecipes = onSearchRecipes,
            onSearchUsers = onSearchUsers,
            onIdentifyIngredientsInPicture = onIdentifyIngredientsInPicture,
            onConfirmIngredients = { ingredientsList -> ingredients += ingredientsList },
            onCamera = { showCameraScreen = true },
            onAddRecipeToCollections = onAddRecipeToCollections,
            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
            onSearchRecipesClear = onSearchRecipesClear,
            onSearchUsersClear = onSearchUsersClear,
            onIngredientsClear = {
                ingredients = emptySet()
                onIngredientsClear()
            },
            onRecipeCollectionsClear = onRecipeCollectionsClear,
            onUserProfileRequest = onUserProfileRequest,
            onRecipeProfileRequest = onRecipeProfileRequest,
            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
            enableButtons = enableButtons
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        recipesResultState = apiSuccess(
            listOf(
                RecipeInfo(
                    id = 1,
                    name = "Recipe Name",
                    authorUsername = "ChefBear",
                    rating = 4.3,
                    cuisine = Cuisine.MEDITERRANEAN,
                    mealType = MealType.SIDE_DISH,
                    preparationTime = 30,
                    servings = 4,
                    picture = Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato))
                ),
                RecipeInfo(
                    id = 1,
                    name = "Recipe Name",
                    authorUsername = "ChefBear",
                    rating = 4.3,
                    cuisine = Cuisine.MEDITERRANEAN,
                    mealType = MealType.SIDE_DISH,
                    preparationTime = 30,
                    servings = 4,
                    picture = Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato))
                )
            )
        ),
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
        recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
            collectionsToAddRecipeState = apiSuccess(emptyList()),
            collectionsToRemoveRecipeState = apiSuccess(emptyList())
        ),
        enableButtons = true,
    )
}
