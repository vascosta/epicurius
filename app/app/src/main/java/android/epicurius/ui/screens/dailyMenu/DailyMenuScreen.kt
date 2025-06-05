package android.epicurius.ui.screens.dailyMenu

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
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
    onBackButton: () -> Unit,
    onRecipeRequest: (Int) -> Unit,
    onDailyMenuRefresh: () -> Unit,
    menuState: LoadState<Map<String, RecipeInfo?>>,
) {
    Scaffold(
        topBar = { TopBar("Today's Menu", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = menuState,
                swipeToRefresh = onDailyMenuRefresh,
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
                        MenuItemBox("Breakfast", menu["Breakfast"], onRecipeRequest)
                        MenuItemBox("Soup", menu["Soup"], onRecipeRequest)
                        MenuItemBox("Lunch", menu["Lunch"], onRecipeRequest)
                        MenuItemBox("Dinner", menu["Dinner"], onRecipeRequest)
                        MenuItemBox("Dessert", menu["Dessert"], onRecipeRequest)
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
            picture = "".toByteArray()
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
            picture = "".toByteArray()
        )
    )
    DailyMenuScreen({}, {}, {}, apiSuccess(menu))
}
