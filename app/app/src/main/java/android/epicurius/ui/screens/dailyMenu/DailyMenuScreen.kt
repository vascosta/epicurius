package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.dailyMenu.components.MenuItemBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DailyMenuScreen(
    menuState: LoadState<Map<String, RecipeInfo?>>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onBackButton: () -> Unit = {},
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRecipeCollectionsClear: () -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = {},
    onRecipeRequest: (recipeId: Int) -> Unit = {},
    enableButtons: Boolean
) {
    Scaffold(
        topBar = { TopBar(
            titleText = "Today's Menu",
            backButton = true,
            enableButtons = enableButtons,
            onBackButton = onBackButton
        ) },
        bottomBar = { BottomBar(buttonsEnable = enableButtons && menuState is Loaded) },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = menuState,
                content = { menu ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .background(Color.White)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MenuItemBox(
                            title = "Breakfast",
                            recipe = menu["breakfast"],
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onCollectionsClear = onRecipeCollectionsClear,
                            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                            onRecipeRequest = onRecipeRequest,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Soup",
                            recipe = menu["soup"],
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onCollectionsClear = onRecipeCollectionsClear,
                            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                            onRecipeRequest = onRecipeRequest,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Lunch",
                            recipe = menu["lunch"],
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onCollectionsClear = onRecipeCollectionsClear,
                            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                            onRecipeRequest = onRecipeRequest,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Dinner",
                            recipe = menu["dinner"],
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onCollectionsClear = onRecipeCollectionsClear,
                            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                            onRecipeRequest = onRecipeRequest,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Dessert",
                            recipe = menu["dessert"],
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onCollectionsClear = onRecipeCollectionsClear,
                            onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                            onRecipeRequest = onRecipeRequest,
                            enableButtons = enableButtons
                        )
                    }
                }
            )
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun DailyMenuPreview() {
    val menu = mapOf(
        "Breakfast" to RecipeInfo(
            id = 1,
            name = "Pancakes",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.AMERICAN,
            mealType = MealType.BREAKFAST,
            preparationTime = 20,
            servings = 2,
            picture = ""
        ),
        "Lunch" to RecipeInfo(
            id = 2,
            name = "Caesar Salad",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 15,
            servings = 1,
            picture = ""
        )
    )
    DailyMenuScreen(
        apiSuccess(menu),
        RecipeCollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        enableButtons = true
    )
}
