package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.user.components.FollowBox
import android.epicurius.ui.screens.user.components.ProfileTabBar
import android.epicurius.ui.screens.user.components.UserProfilePicture
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
    onBackButton: () -> Unit = {},
    userProfile: UserProfile
) {
    Scaffold(
        topBar = {
            TopBar(text = "Profile", backButton = true, onBackButton = onBackButton, icon = Icons.Filled.Settings)
        },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
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

                ProfileTabBar()
            }
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

    UserProfileScreen(userProfile = userProfile)
}