package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.FollowingStatus
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.components.CollectionProfileBox
import android.epicurius.ui.screens.collections.components.CreateCollectionDialog
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.recipe.confirmIngredients.components.InfoDialog
import android.epicurius.ui.screens.user.components.FollowBox
import android.epicurius.ui.screens.user.components.ProfileTabBar
import android.epicurius.ui.screens.user.components.UserProfilePicture
import android.epicurius.ui.screens.user.profile.utils.getFlagEmoji
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.getOrThrow
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UserProfileScreen(
    isAnotherUserProfile: Boolean,
    userProfileVisibility: Boolean,
    userProfileState: LoadState<UserProfile>,
    userRecipesState: LoadState<List<RecipeInfo>>,
    userKitchenBookState: LoadState<List<CollectionProfile>>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onBackButton: () -> Unit = {},
    onUpdateUserProfilePicture: (picture: ByteArray?) -> Unit = {},
    onFollow: (username: String) -> Unit = {},
    onUnfollow: (username: String) -> Unit = {},
    onCancelFollow: (username: String) -> Unit = {},
    onUserKitchenBookCollectionCreate: (collectionName: String) -> Unit = {},
    onUserKitchenBookCollectionDelete: (collectionId: Int) -> Unit = {},
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onUserRecipesClear: () -> Unit = {},
    onUserKitchenBookClear: () -> Unit = {},
    onRecipeCollectionsClear: () -> Unit = {},
    onUserRecipesRequest: (username: String?) -> Unit = {},
    onUserKitchenBookRequest: (username: String?) -> Unit = {},
    onFollowersOrFollowingRequest: (
        tab: Int,
        username: String,
        followersCount: Int,
        followingCount: Int
    ) -> Unit = { _, _, _, _ -> },
    onUserKitchenBookCollectionRequest: (collectionId: Int, isCollectionOwner: Boolean) -> Unit = { _, _ -> },
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = {},
    onRecipeRequest: (recipeId: Int) -> Unit = {},
    enableButtons: Boolean
) {
    val context = LocalContext.current

    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                selectedImageBytes = bytes
                onUpdateUserProfilePicture(bytes)
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

    LaunchedEffect(userProfileVisibility) {
        if (userProfileState is Loaded) {
            val username = userProfileState.getOrThrow().name
            if (userProfileVisibility) {
                onUserRecipesRequest(username)
                onUserKitchenBookRequest
            }
            else {
                onUserRecipesClear()
                onUserKitchenBookClear()
            }
        }
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
                content = { userProfile ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isAnotherUserProfile) {
                                IconButton(
                                    onClick = { showInfoDialog = true },
                                    enabled = enableButtons
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info Icon",
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                    //.align(Alignment.End),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                val flagEmoji = getFlagEmoji(userProfile.country)
                                Text(text = flagEmoji, fontSize = 24.sp)
                            }

                            if (showInfoDialog)
                                InfoDialog(
                                    boldText = "Wanna edit your profile picture?",
                                    normalText = "Double tap on your profile picture to change it or" +
                                            " tap on it to view delete picture option.",
                                    onDismissRequest = { showInfoDialog = false }
                                )
                        }
                        Spacer(modifier = Modifier.fillMaxHeight(0.02f))
                        UserProfilePicture(
                            profilePicture = selectedImageBytes ?: userProfile.profilePictureBytes,
                            iconSize = 120,
                            isUserProfile = !isAnotherUserProfile,
                            onUpdateProfilePicture = {
                                if (!isAnotherUserProfile && galleryPermissionState.status.isGranted) {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else if (!isAnotherUserProfile){
                                    galleryPermissionState.launchPermissionRequest()
                                }
                            },
                            onRemoveImage = { imageBytes ->
                                onUpdateUserProfilePicture(ByteArray(0))
                                selectedImageBytes = null
                            },
                            enabled = enableButtons
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
                                onClick = {
                                    onFollowersOrFollowingRequest(
                                        selectedTabIndex,
                                        userProfile.name,
                                        userProfile.followersCount,
                                        userProfile.followingCount
                                    )
                                },
                                enabled = userProfileVisibility
                            )
                            FollowBox(
                                name = "Following",
                                number = userProfile.followingCount,
                                onClick = {
                                    onFollowersOrFollowingRequest(
                                        selectedTabIndex,
                                        userProfile.name,
                                        userProfile.followersCount,
                                        userProfile.followingCount
                                    )
                                },
                                enabled = userProfileVisibility
                            )
                        }
                        if (isAnotherUserProfile) {
                            val buttonText = when (userProfile.followingStatus) {
                                FollowingStatus.ACCEPTED -> "Unfollow"
                                FollowingStatus.PENDING -> "Cancel Follow"
                                FollowingStatus.NOT_FOLLOWING -> "Follow"
                            }
                            Button(
                                onClick = {
                                    when (userProfile.followingStatus) {
                                        FollowingStatus.ACCEPTED -> onUnfollow(userProfile.name)
                                        FollowingStatus.PENDING -> onCancelFollow(userProfile.name)
                                        FollowingStatus.NOT_FOLLOWING -> onFollow(userProfile.name)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                            ) { Text(text = buttonText) }
                        } else
                            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                        if (userProfileVisibility) {
                            ProfileTabBar(
                                selectedTabIndex = selectedTabIndex,
                                onRecipesClick = { selectedTabIndex = 0 },
                                onKitchenBookClick = { selectedTabIndex = 1 },
                                enabled = enableButtons
                            )
                            Spacer(Modifier.size(10.dp))
                            if (selectedTabIndex == 0) {
                                LoadStateRenderer(
                                    loadState = userRecipesState,
                                    content = { recipes ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            if (recipes.isNotEmpty()) {
                                                recipes.forEach { recipe ->
                                                    RecipeInfoBox(
                                                        collectionId = null,
                                                        recipeInfo = recipe,
                                                        recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                                                        onAddRecipeToCollections = onAddRecipeToCollections,
                                                        onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                                                        onRecipeCollectionsClear = onRecipeCollectionsClear,
                                                        onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                                                        onRecipeRequest = onRecipeRequest,
                                                        enableButtons = enableButtons
                                                    )
                                                }
                                                Button(
                                                    onClick = { onUserRecipesRequest(userProfile.name) },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    enabled = enableButtons
                                                ) { Text("Load More") }
                                            }
                                            else if (userRecipesState is Loaded) {
                                                Text(
                                                    "User has no recipes yet.",
                                                    color = Color.Gray,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                )
                            } else if (selectedTabIndex == 1) {
                                if (!isAnotherUserProfile) {
                                    Row {
                                        Spacer(Modifier
                                            .fillMaxWidth()
                                            .weight(0.9f))
                                        IconButton(
                                            onClick = { showCreateCollectionDialog = true },
                                            enabled = enableButtons
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Create Collection"
                                            )
                                        }
                                    }
                                }
                                LoadStateRenderer(
                                    loadState = userKitchenBookState,
                                    content = { collections ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            if (collections.isNotEmpty()) {
                                                collections.forEach { collection ->
                                                    CollectionProfileBox(
                                                        isCollectionOwner = !isAnotherUserProfile,
                                                        collection = collection,
                                                        onCollectionDelete = onUserKitchenBookCollectionDelete,
                                                        onCollectionRequest = onUserKitchenBookCollectionRequest,
                                                        enableButtons = enableButtons
                                                    )
                                                }
                                                Button(
                                                    onClick = { onUserKitchenBookRequest(userProfile.name) },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    enabled = enableButtons
                                                ) { Text("Load More") }
                                            }
                                            else if (userKitchenBookState is Loaded) {
                                                Text(
                                                    "User has no kitchen book collections yet.",
                                                    color = Color.Gray,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                )
                                if (showCreateCollectionDialog) {
                                    CreateCollectionDialog(
                                        onCollectionCreate = onUserKitchenBookCollectionCreate,
                                        onDismiss = { showCreateCollectionDialog = false },
                                        enableButtons = enableButtons
                                    )
                                }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
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
        country = "US",
        privacy = false,
        profilePicture = null,
        followersCount = 100,
        followingCount = 50,
        followingStatus = FollowingStatus.ACCEPTED
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
            picture = ""
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
            picture = ""
        ),
        RecipeInfo(
            id = 3,
            name = "Vegetable Stir Fry",
            authorUsername = "John Doe",
            rating = 4.5,
            cuisine = Cuisine.CHINESE,
            mealType = MealType.SIDE_DISH,
            preparationTime = 20,
            servings = 3,
            picture = ""
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
        userProfileVisibility = true,
        userProfileState = apiSuccess(userProfile),
        userRecipesState = apiSuccess(userRecipes),
        userKitchenBookState = apiSuccess(kitchenBookCollections),
        recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
            collectionsToAddRecipeState = apiSuccess(emptyList()),
            collectionsToRemoveRecipeState = apiSuccess(emptyList())
        ),
        enableButtons = true
    )
}