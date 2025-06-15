package android.epicurius.ui.screens.user.settings

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.domain.user.UserProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.user.models.input.UpdateUserInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.user.UserViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): UserViewModel(service, session, context) {

    private val userInfoFlow = MutableStateFlow<LoadState<UserInfo>>(idle())
    val userInfo = userInfoFlow.asStateFlow()

    fun updateUser(
        name: String?,
        email: String?,
        country: String?,
        password: String?,
        confirmPassword: String?,
        privacy: Boolean?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?
    ) {
        userInfoFlow.value = loading()
        disableButtons()
        if (!validateUpdateUserInfo(name, email, password, confirmPassword)) {
            enableButtons()
            return
        }
        val updateUserInfo = UpdateUserInputModel(name, email, country, password, privacy, intolerances, diets)
        viewModelScope.launch {
            handleUpdateUser(updateUserInfo)
        }

    }

    fun deleteAccount() {

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

    private suspend fun handleUpdateUser(updateUserInfo: UpdateUserInputModel) {
        val result = request {
            val token = session.getToken()
            service.userService.updateUser(token, updateUserInfo)
        }
        when {
            result.isSuccess -> {
                val oldUserInfo = session.getUserInfo()
                val newUserInfo = oldUserInfo.copy(
                    name = updateUserInfo.name ?: oldUserInfo.name,
                    email = updateUserInfo.email ?: oldUserInfo.email,
                    country = updateUserInfo.country ?: oldUserInfo.country,
                    privacy = updateUserInfo.privacy ?: oldUserInfo.privacy,
                    intolerances = updateUserInfo.intolerances?.toList() ?: oldUserInfo.intolerances,
                    diets = updateUserInfo.diets?.toList() ?: oldUserInfo.diets,
                    profilePictureName = oldUserInfo.profilePictureName
                )
                session.updateUserInfo(newUserInfo)
                userInfoFlow.value = apiSuccess(newUserInfo)
            }
        }
        enableButtons()
    }

    private fun validateUpdateUserInfo(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?
    ): Boolean =
        when {
            password != null && confirmPassword != null && password != confirmPassword -> {
                showToast("passwords must be equal")
                false
            }
            name != null && !validateName(name) -> false
            email != null && !validateEmail(email) -> false
            password != null && !validatePassword(password) -> false
            else -> true
        }
}