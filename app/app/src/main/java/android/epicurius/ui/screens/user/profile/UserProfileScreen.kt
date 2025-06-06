package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserProfileScreen(
    isAnotherUserProfile: Boolean,
    userProfileVisibility: Boolean,
    //followEnable: Boolean,
    onBackButton: () -> Unit,
    onSettingsButton: () -> Unit,
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
                            FollowBox("Followers", userProfile.followersCount)
                            FollowBox("Following", userProfile.followingCount)
                        }

                        Spacer(modifier = Modifier.fillMaxHeight(0.03f))

                        if (userProfileVisibility) {
                            ProfileTabBar()
                        }

                    }
                }
            )
        }
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

    UserProfileScreen(false, true, {}, {}, {}, {}, {}, {}, apiSuccess(userProfile))
}