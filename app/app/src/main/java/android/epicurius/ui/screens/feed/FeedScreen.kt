package android.epicurius.ui.screens.feed

import android.content.Intent
import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.dailyMenu.DailyMenuActivity
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FeedScreen(
    userFeedState: LoadState<List<RecipeInfo>>,
    collectionsState: LoadState<List<CollectionProfile>>,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onRecipeRequest: (Int) -> Unit,
    onUserFeedRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    enableButtons: Boolean
) {
    val context = LocalContext.current

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
                            .padding(16.dp)
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    context.startActivity(Intent(context, DailyMenuActivity::class.java))
                                }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.menu),
                                    contentDescription = "Daily Menu",
                                    modifier = Modifier.size(45.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        userFeed.forEach { recipe ->
                            Spacer(modifier = Modifier.size(10.dp))
                            RecipeInfoBox(
                                collectionId = null,
                                recipeInfo = recipe,
                                collectionsState = collectionsState,
                                onAddRecipeToCollection = onAddRecipeToCollection,
                                onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                                onRecipeRequest = onRecipeRequest,
                                enableButtons = enableButtons
                            )
                        }
                        Button(
                            onClick = { onLoadMore() },
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                        ) { Text("Load More") }
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

    val collection = listOf(
        CollectionProfile(
            id = 1,
            name = "Favourites",
        )
    )

    FeedScreen(apiSuccess(recipeList), apiSuccess(collection), {_, _ ->}, {_, _ ->}, {}, {}, {}, true)
}