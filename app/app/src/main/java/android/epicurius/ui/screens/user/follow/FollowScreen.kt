package android.epicurius.ui.screens.user.follow

import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.user.follow.components.FollowTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FollowScreen(
    selectedTab: Int,
    onBackButton: () -> Unit = {},
    userProfile: UserProfile,
    followers: List<FollowUser>,
    following: List<FollowingUser>
) {
    var selectedTabIndex by remember { mutableIntStateOf(selectedTab) }

    Scaffold(
        topBar = {
            FollowTopBar(
                following = userProfile.followingCount,
                followers = userProfile.followersCount,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                onBackButton = onBackButton,
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val usersToShow = if (selectedTabIndex == 0) followers else following

                usersToShow.forEach { user -> UserBox(user = user) }
            }
        },
        containerColor = Color.White
    )
}


@Preview
@Composable
fun FollowPreview() {
    val userProfile = UserProfile(
        name = "John Doe",
        country = "USA",
        privacy = false,
        profilePicture = null,
        followersCount = 100,
        followingCount = 50
    )

    val followers = listOf(
        FollowUser(
            id = 1,
            name = "Jane Smith",
            profilePicture = null
        )
    )

    val following = listOf(
        FollowingUser(
            id = 2,
            name = "Alice Johnson",
            profilePicture = null
        )
    )

    FollowScreen(0, {}, userProfile, followers, following)
}
