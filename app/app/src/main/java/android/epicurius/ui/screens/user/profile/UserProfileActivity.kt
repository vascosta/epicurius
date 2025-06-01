package android.epicurius.ui.screens.user.profile

import android.epicurius.MainActivity
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UserProfileScreen(
                    onBackButton = { navigateTo<MainActivity>() },
                    userProfile = UserProfile(
                        name = "John Doe",
                        country = "USA",
                        privacy = false,
                        profilePicture = null,
                        followersCount = 100,
                        followingCount = 50
                    )
                )
            }
        }
    }
}