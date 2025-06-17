package android.epicurius.ui.screens.feed

import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.components.CollectionsStateBundle
import android.epicurius.ui.screens.feed.components.FollowRequestDialog
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.Loading
import android.epicurius.ui.screens.utils.LoadingSpinner
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    collectionsStateBundle: CollectionsStateBundle,
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
    onDailyMenuRequest: () -> Unit,
    onCollectionsClear: () -> Unit,
    onUserFeedRefresh: () -> Unit,
    onFollowRequests: () -> List<SearchUser>,
    onAcceptFollowRequest: (userId: Int) -> Unit,
    onRejectFollowRequest: (userId: Int) -> Unit,
    enableButtons: Boolean
) {
    var showLoadingSpinnerOnLoadMore by remember { mutableStateOf(!enableButtons) }
    var enableLoadMoreButton by remember { mutableStateOf(!enableButtons) }
    var showFollowRequestsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userFeedState) {
        if (userFeedState is Loaded) {
            enableLoadMoreButton = true
            showLoadingSpinnerOnLoadMore = false
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                titleText = "For you to cook",
                enableButtons = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
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
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(30.dp),
                            tint = Color(0xFFAC88DC)
                        )
                    }
                    IconButton(
                        onClick = onDailyMenuRequest
                    ) {
                        Image(
                            painter = painterResource(R.drawable.menu),
                            contentDescription = "Daily Menu",
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                LoadStateRenderer(
                    loadState = userFeedState,
                    swipeToRefresh = onUserFeedRefresh,
                    content = { userFeed ->
                        if (userFeed.isNotEmpty()) {
                            userFeed.forEach { recipe ->
                                Spacer(modifier = Modifier.size(10.dp))
                                RecipeInfoBox(
                                    collectionId = null,
                                    recipeInfo = recipe,
                                    collectionsStateBundle = collectionsStateBundle,
                                    onAddRecipeToCollections = onAddRecipeToCollections,
                                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                                    onRecipeRequest = onRecipeRequest,
                                    onCollectionsRequest = onCollectionsRequest,
                                    onRemoveRecipeFromCollection = {_, _ ->},
                                    onCollectionsClear = onCollectionsClear,
                                    enableButtons = enableButtons
                                )
                            }
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
                Button(
                    onClick = {
                        onUserFeedRefresh()
                        showLoadingSpinnerOnLoadMore = true
                        enableLoadMoreButton = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    enabled = enableLoadMoreButton
                ) {
                    if (!showLoadingSpinnerOnLoadMore) {
                        Text("Load More")
                    }
                    else { LoadingSpinner(Modifier.size(30.dp)) }
                }

                if (showFollowRequestsDialog) {
                    FollowRequestDialog(
                        onDismiss = { showFollowRequestsDialog = false },
                        onFollowRequests = onFollowRequests,
                        onAccept = onAcceptFollowRequest,
                        onReject = onRejectFollowRequest
                    )
                }
            }
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
            picture = "",
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
            picture = "",
            isInCollection = false
        )
    )

    FeedScreen(
        apiSuccess(recipeList),
        CollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        { _, _, _, _ -> },
        { _, _, _, _ -> },
        {},
        {},
        {},
        {},
        {},
        { listOf(
            SearchUser(1, "TesteUser12345678901", null),
            SearchUser(2, "AnotherUser1234567890", null),
            SearchUser(4, "ShortUser", null),
        ) },
        {},
        {},
        true
    )
}