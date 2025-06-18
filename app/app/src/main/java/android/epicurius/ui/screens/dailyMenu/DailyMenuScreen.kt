package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.list.components.CollectionsStateBundle
import android.epicurius.ui.screens.dailyMenu.components.MenuItemBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
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
    collectionsStateBundle: CollectionsStateBundle,
    onBackButton: () -> Unit,
    onAddRecipeToCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    onRemoveRecipeFromCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    onRecipeRequest: (recipeId: Int) -> Unit,
    onCollectionsRequest: (recipeId: Int) -> Unit,
    onCollectionsClear: () -> Unit,
    enableButtons: Boolean
) {
    Scaffold(
        topBar = { TopBar(
            titleText = "Today's Menu",
            backButton = true,
            enableButtons = enableButtons,
            onBackButton = onBackButton
        ) },
        bottomBar = { BottomBar(buttonsEnable = enableButtons) },
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
                            collectionsStateBundle = collectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeRequest = onRecipeRequest,
                            onCollectionsRequest = onCollectionsRequest,
                            onCollectionsClear = onCollectionsClear,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Soup",
                            recipe = menu["soup"],
                            collectionsStateBundle = collectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeRequest = onRecipeRequest,
                            onCollectionsRequest = onCollectionsRequest,
                            onCollectionsClear = onCollectionsClear,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Lunch",
                            recipe = menu["lunch"],
                            collectionsStateBundle = collectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeRequest = onRecipeRequest,
                            onCollectionsRequest = onCollectionsRequest,
                            onCollectionsClear = onCollectionsClear,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Dinner",
                            recipe = menu["dinner"],
                            collectionsStateBundle = collectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeRequest = onRecipeRequest,
                            onCollectionsRequest = onCollectionsRequest,
                            onCollectionsClear = onCollectionsClear,
                            enableButtons = enableButtons
                        )
                        MenuItemBox(
                            title = "Dessert",
                            recipe = menu["dessert"],
                            collectionsStateBundle = collectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeRequest = onRecipeRequest,
                            onCollectionsRequest = onCollectionsRequest,
                            onCollectionsClear = onCollectionsClear,
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
            picture = "",
            isInCollection = false
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
            picture = "",
            isInCollection = true
        )
    )
    DailyMenuScreen(
        apiSuccess(menu),
        CollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        {},
        { _, _, _, _ -> },
        { _, _, _, _ -> },
        {},
        {},
        {},
        {},
        true
    )
}
