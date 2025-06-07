package android.epicurius.ui.screens.feed

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FeedScreen(
    userFeedState: LoadState<List<RecipeInfo>>,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onRecipeRequest: (Int) -> Unit,
    onUserFeedRefresh: () -> Unit,
    enableButtons: Boolean
) {
    Scaffold(
        topBar = { TopBar(text = "For you to cook") },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = userFeedState,
                swipeToRefresh = onUserFeedRefresh,
                content = { userFeed ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        userFeed.forEach { recipe ->
                            RecipeInfoBox(
                                collectionId = null,
                                recipeInfo = recipe,
                                onAddRecipeToCollection = onAddRecipeToCollection,
                                onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                                onRecipeRequest = onRecipeRequest,
                                enableButtons = enableButtons
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun FeedPreview() {
    val recipeList = listOf(
        RecipeInfo(
            id = 1,
            name = "Spaghetti Bolognese",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = "".toByteArray(),
            isInCollection = true
        ),
        RecipeInfo(
            id = 2,
            name = "Chicken Curry",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.INDIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 45,
            servings = 4,
            picture = "".toByteArray(),
            isInCollection = false
        )
    )

    FeedScreen(apiSuccess(recipeList), {_, _ ->}, {_, _ ->}, {}, {}, true)
}