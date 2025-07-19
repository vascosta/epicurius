package android.epicurius.ui.screens.feed

import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.feed.components.FollowRequestDialog
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FeedScreen(
    userFeedState: LoadState<List<RecipeInfo>>,
    followRequestsState: LoadState<List<SearchUser>>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRecipeCollectionsClear: () -> Unit = {},
    onAcceptFollowRequest: (name: String) -> Unit = {},
    onRejectFollowRequest: (name: String) -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = {},
    onFollowRequests: () -> Unit = {},
    onRecipeRequest: (recipeId: Int) -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onDailyMenuRequest: () -> Unit = {},
    onLoadMoreUserFeed: () -> Unit = {},
    enableButtons: Boolean
) {
    var showFollowRequestsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "For you to cook",
                enableButtons = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons && userFeedState is Loaded) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Beige)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showFollowRequestsDialog = true },
                        enabled = enableButtons
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(30.dp),
                            tint = DarkPurple
                        )
                    }
                    IconButton(
                        onClick = onDailyMenuRequest,
                        enabled = enableButtons
                    ) {
                        Image(
                            painter = painterResource(R.drawable.menu),
                            contentDescription = "Daily Menu",
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                LoadStateRenderer(
                    loadState = userFeedState,
                    content = { userFeed ->
                        if (userFeed.isNotEmpty()) {
                            userFeed.forEach { recipe ->
                                Spacer(modifier = Modifier.size(10.dp))
                                RecipeInfoBox(
                                    collectionId = null,
                                    recipeInfo = recipe,
                                    recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                                    onAddRecipeToCollections = onAddRecipeToCollections,
                                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                                    onRecipeRequest = onRecipeRequest,
                                    onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                                    onRemoveRecipeFromCollection = {_, _ ->},
                                    onRecipeCollectionsClear = onRecipeCollectionsClear,
                                    enableButtons = enableButtons
                                )
                            }
                            Button(
                                onClick = { onLoadMoreUserFeed() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Load More") }
                        } else if (userFeedState is Loaded) {
                            Text(
                                text = "No recipes for you to cook, how about following new users?",
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                )
                if (showFollowRequestsDialog) {
                    FollowRequestDialog(
                        followRequestsState = followRequestsState,
                        onAcceptFollowRequest = onAcceptFollowRequest,
                        onRejectFollowRequest = onRejectFollowRequest,
                        onFollowRequests = onFollowRequests,
                        onUserProfileRequest = onUserProfileRequest,
                        onDismiss = { showFollowRequestsDialog = false },
                        enableButtons = enableButtons
                    )
                }
            }
        },
        containerColor = Beige
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
            picture = ""
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
            picture = ""
        )
    )

    FeedScreen(
        apiSuccess(recipeList),
        apiSuccess(emptyList()),
        RecipeCollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        onFollowRequests = { listOf(
            SearchUser(1, "TesteUser12345678901", null),
            SearchUser(2, "AnotherUser1234567890", null),
            SearchUser(4, "ShortUser", null),
        ) },
        enableButtons = true
    )
}