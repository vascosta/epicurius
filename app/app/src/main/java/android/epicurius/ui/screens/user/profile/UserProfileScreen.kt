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
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UserProfileScreen(
    isAnotherUserProfile: Boolean,
    isFollower: Boolean,
    userProfileVisibility: Boolean,
    userRecipes: LoadState<List<RecipeInfo>>?,
    recipeCollectionsState: LoadState<List<CollectionProfile>>?,
    kitchenBookCollectionsState: LoadState<List<CollectionProfile>>?,
    //followEnable: Boolean,
    onBackButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onFollowersButton: () -> Unit,
    onFollowingButton: () -> Unit,
    onFollowRequest: (String) -> Unit,
    //onFollow: (String) -> Unit,
    //onUnfollow: (String) -> Unit,
    onCollectionRequest: (Int) -> Unit,
    //onRecipeRequest: (Int) -> Unit,
    //onAddRecipeToCollectionRequest: (Int, Int) -> Unit,
    onUserProfileRefresh: () -> Unit,
    onUserPictureChange: (ByteArray) -> Unit,
    onUserRecipesLoadMore: () -> Unit,
    onUserKitchenBookLoadMore: () -> Unit,
    userProfileState: LoadState<UserProfile>,
    //userRecipesState: LoadState<List<RecipeInfo>>,
    //userKitchenBookState: LoadState<List<CollectionProfile>>
    //kitchenBookCollectionRecipesState: LoadState<List<RecipeInfo>>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                selectedImageBytes = bytes
                onUserPictureChange(bytes)
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Profile",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = true,
                icon = if (!isAnotherUserProfile) Icons.Filled.Settings else null,
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
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
                        UserProfilePicture(
                            profilePicture = selectedImageBytes ?: userProfile.profilePicture,
                            iconSize = 120,
                            onClick = {
                                if (!isAnotherUserProfile && galleryPermissionState.status.isGranted) {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else if (!isAnotherUserProfile){
                                    galleryPermissionState.launchPermissionRequest()
                                }
                            }
                        )
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
                                onClick = onFollowersButton,
                                enabled = userProfileVisibility
                            )
                            FollowBox(
                                name = "Following",
                                number = userProfile.followingCount,
                                onClick = onFollowingButton,
                                enabled = userProfileVisibility
                            )
                        }
                        if (isAnotherUserProfile) {
                            val buttonText = if (isFollower) "Unfollow" else "Follow"
                            val buttonColor = if (isFollower) Color.Black else Color.Unspecified

                            Button(
                                onClick = { onFollowRequest(userProfile.name) },
                                modifier = Modifier.fillMaxWidth().padding(5.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                            ) {
                                Text(text = buttonText)
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                        }

                        if (userProfileVisibility) {
                            ProfileTabBar(
                                selectedTabIndex = selectedTabIndex,
                                onRecipesClick = { selectedTabIndex = 0 },
                                onKitchenBookClick = { selectedTabIndex = 1 },
                            )
                            Spacer(Modifier.size(10.dp))

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
                                                        collectionsStateBundle = null,
                                                        onAddRecipeToCollections = {_, _, _, _ ->},
                                                        onRemoveRecipeFromCollections = {_, _, _, _ ->},
                                                        onRemoveRecipeFromCollection = {_, _ ->},
                                                        onRecipeRequest = { _ -> },
                                                        onCollectionsRequest = {},
                                                        onCollectionsClear = {},
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
                                                        enableButtons = true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 0.5.dp,
                                color = Color.Black
                            )
                            Text(
                                text = "This profile is private.",
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth().padding(5.dp),
                                textAlign = TextAlign.Center
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
            picture = "",
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
            picture = "",
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
        isAnotherUserProfile = false,
        isFollower = false,
        userProfileVisibility = false,
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
        {},
        {},
        apiSuccess(userProfile)
    )
}