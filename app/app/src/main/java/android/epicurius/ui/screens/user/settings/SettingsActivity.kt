package android.epicurius.ui.screens.user.settings

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.favourites.folder.FavouritesActivity
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
                    onBackButton = { finish() },
                    onFavouritesRequest = { navigateTo<FavouritesActivity>() },
                    onLogout = { viewModel.logout() },
                    buttonsEnable = viewModel.buttonsEnable
                )
            }
        }
    }
}