package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.favourites.folder.components.CollectionProfileBox
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.user.components.FollowBox
import android.epicurius.ui.screens.user.components.ProfileTabBar
import android.epicurius.ui.screens.user.components.UserProfilePicture
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileScreen(
    isAnotherUserProfile: Boolean,
    userProfileVisibility: Boolean,
    userRecipes: LoadState<List<RecipeInfo>>?,
    recipeCollectionsState: LoadState<List<CollectionProfile>>?,
    kitchenBookCollectionsState: LoadState<List<CollectionProfile>>?,
    //followEnable: Boolean,
    onBackButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onFollowersButton: () -> Unit,
    onFollowingButton: () -> Unit,
    //onFollow: (String) -> Unit,
    //onUnfollow: (String) -> Unit,
    onCollectionRequest: (Int) -> Unit,
    //onRecipeRequest: (Int) -> Unit,
    //onAddRecipeToCollectionRequest: (Int, Int) -> Unit,
    onUserProfileRefresh: () -> Unit,
    onUserRecipesLoadMore: () -> Unit,
    onUserKitchenBookLoadMore: () -> Unit,
    userProfileState: LoadState<UserProfile>,
    //userRecipesState: LoadState<List<RecipeInfo>>,
    //userKitchenBookState: LoadState<List<CollectionProfile>>
    //kitchenBookCollectionRecipesState: LoadState<List<RecipeInfo>>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                text = "Profile",
                backButton = true,
                onBackButton = onBackButton,
                icon = if (!isAnotherUserProfile) Icons.Filled.Settings else null,
                onIconClick = onSettingsButton
            )
        },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = userProfileState,
                swipeToRefresh = onUserProfileRefresh,
                content = { userProfile ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                        UserProfilePicture(userProfile.profilePicture, 120)

                        Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                        Text(text = userProfile.name, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.fillMaxHeight(0.05f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            FollowBox(
                                name = "Followers",
                                number = userProfile.followersCount,
                                onClick = onFollowersButton
                            )
                            FollowBox(
                                name = "Following",
                                number = userProfile.followingCount,
                                onClick = onFollowingButton
                            )
                        }

                        Spacer(modifier = Modifier.fillMaxHeight(0.03f))

                        if (userProfileVisibility) {
                            ProfileTabBar(
                                selectedTabIndex = selectedTabIndex,
                                onRecipesClick = { selectedTabIndex = 0 },
                                onKitchenBookClick = { selectedTabIndex = 1 },
                            )
                            Spacer(Modifier.size(10.dp))
                        }

                        if (selectedTabIndex == 0 && userRecipes != null) {
                            LoadStateRenderer(
                                loadState = userRecipes,
                                content = { recipes ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        if (recipes.isEmpty()) {
                                            Text(
                                                "User has no recipes yet.",
                                                color = Color.Gray,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            recipes.forEach { recipe ->
                                                RecipeInfoBox(
                                                    collectionId = null,
                                                    recipeInfo = recipe,
                                                    collectionsState = recipeCollectionsState,
                                                    onAddRecipeToCollection = { _, _ ->},
                                                    onRemoveRecipeFromCollection = { _, _ ->},
                                                    onRecipeRequest = { _ -> },
                                                    onCollectionsRequest = { _, _ -> },
                                                    enableButtons = true
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        } else if (selectedTabIndex == 1 && kitchenBookCollectionsState != null) {
                            LoadStateRenderer(
                                loadState = kitchenBookCollectionsState,
                                content = { collections ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        if (collections.isEmpty()) {
                                            Text(
                                                "User has no kitchen book collections yet.",
                                                color = Color.Gray,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            collections.forEach { collection ->
                                                CollectionProfileBox(
                                                    collection = collection,
                                                    onCollectionRequest = onCollectionRequest,
                                                    onCollectionDelete = { _ -> },
                                                    buttonsEnable = true
                                                )
                                            }
                                        }
                                    }
                                }
                            )
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
fun UserProfilePreview() {
    val userProfile = UserProfile(
        name = "John Doe",
        country = "USA",
        privacy = false,
        profilePicture = null,
        followersCount = 100,
        followingCount = 50
    )

    val userRecipes = listOf(
        RecipeInfo(
            id = 1,
            name = "Spaghetti Carbonara",
            authorUsername = "John Doe",
            rating = 3.5,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 45,
            servings = 4,
            picture = ByteArray(0),
            isInCollection = false,
        ),
        RecipeInfo(
            id = 2,
            name = "Chicken Curry",
            authorUsername = "John Doe",
            rating = 4.0,
            cuisine = Cuisine.INDIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 2,
            picture = ByteArray(0),
            isInCollection = false,
        )
    )

    val kitchenBookCollections = listOf(
        CollectionProfile(
            id = 1,
            name = "My Kitchen Book",
        )
    )

    UserProfileScreen(
        false,
        true,
        apiSuccess(userRecipes),
        null,
        apiSuccess(kitchenBookCollections),
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        apiSuccess(userProfile)
    )
}