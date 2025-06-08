package android.epicurius.ui.screens.user.settings

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.user.UserViewModel
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): UserViewModel(service, session, context) {

    fun changeUsername() {

    }

    fun changeEmail() {

    }

    fun changePassword() {

    }

    fun changeCountry() {

    }

    fun changePrivacy() {

    }

    fun changeIntolerances() {

    }

    fun changeDiets() {

    }

    fun logout() {
        disableButtons()
        viewModelScope.launch {
            handleLogout()
        }
    }

    private suspend fun handleLogout() {
        val result = request {
            val token = session.getToken()
            service.authService.logout(token)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                onSessionExpired(false)
            }
        }
    }

}