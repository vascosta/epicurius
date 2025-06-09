package android.epicurius.ui.screens.user.settings

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.favourites.folder.FavouritesActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class SettingsActivity : EpicuriusActivity() {
    override val viewModel: SettingsViewModel by getViewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SettingsScreen(
                    user = UserInfo(
                        name = "User",
                        email = "user@email.com",
                        country = "PT",
                        privacy = true,
                        intolerances = listOf(
                            Intolerance.GLUTEN,
                            Intolerance.DAIRY
                        ),
                        diets = listOf(
                            Diet.GLUTEN_FREE
                        ),
                        profilePictureName = ""
                    ),
                    onBackButton = { navigateTo<UserProfileActivity>(true) },
                    onFavouritesRequest = { navigateTo<FavouritesActivity>() },
                    onUserUpdate = { username, email, country, password, confirmPassword, privacy, intolerances, diets ->
                    },
                    onLogout = { viewModel.logout() },
                    onDeleteAccount = {  },
                    buttonsEnable = viewModel.buttonsEnable
                )
            }
        }
    }
}