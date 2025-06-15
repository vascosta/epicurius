package android.epicurius.ui.screens.user.settings

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.favourites.folder.FavouritesActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsActivity : EpicuriusActivity() {
    override val viewModel: SettingsViewModel by getViewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userInfo.collectLatest { state ->
                if (state is Idle) viewModel.getUserInfo()
            }
        }
        setContent {
            val userInfoState = viewModel.userInfo.collectAsState(idle())
            MaterialTheme {
                SettingsScreen(
                    userInfoState = userInfoState.value,
                    onBackButton = { navigateTo<UserProfileActivity>(true) },
                    onFavouritesRequest = { navigateTo<FavouritesActivity>() },
                    onUserUpdate = {
                        name: String?, email: String?, country: String?, password: String?,
                        confirmPassword: String?, privacy: Boolean?,
                        intolerances: Set<Intolerance>?, diets: Set<Diet>? ->
                        viewModel.updateUser(
                            name,
                            email,
                            country,
                            password,
                            confirmPassword,
                            privacy,
                            intolerances,
                            diets
                        )
                    },
                    onLogout = { viewModel.logout() },
                    onDeleteAccount = { viewModel.deleteAccount() },
                    enableButtons = viewModel.enableButtons
                )
            }
        }
    }
}