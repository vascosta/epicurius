package android.epicurius.ui.screens.user.follow

import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class FollowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FollowScreen(
                    selectedTab = 0,
                    onBackButton = { navigateTo<UserProfileActivity>() },
                    userProfile = UserProfile(
                            name = "John Doe",
                            country = "USA",
                            privacy = false,
                            profilePicture = null,
                            followersCount = 100,
                            followingCount = 50
                    ),
                    followers = listOf(
                        FollowUser(
                            id = 1,
                            name = "Jane Smith",
                            profilePicture = null
                        )
                    ),
                    following = listOf(
                        FollowingUser(
                            id = 2,
                            name = "Alice Johnson",
                            profilePicture = null
                        )
                    )
                )
            }
        }
    }
}