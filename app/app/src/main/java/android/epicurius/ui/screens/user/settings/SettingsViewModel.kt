package android.epicurius.ui.screens.user.settings

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.user.models.input.UpdateUserInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.user.UserViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
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

    fun getUserInfo() {
        disableButtons()
        userInfoFlow.value = loading()
        viewModelScope.launch { getCachedUserInfo() }
    }

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
        disableButtons()
        if (
            (name == null && email == null && country == null && password == null &&
            confirmPassword == null && privacy == null && intolerances == null && diets == null)
        ) {
            enableButtons()
            showToast("give new information before updating")
            return
        }
        if (!validateUpdateUserInfo(name, email, password, confirmPassword)) {
            enableButtons()
            return
        }
        userInfoFlow.value = loading()
        val updateUserInfo = UpdateUserInputModel(name, email, country, password, privacy, intolerances, diets)
        viewModelScope.launch { handleUpdateUser(updateUserInfo) }

    }

    fun deleteAccount() {
        disableButtons()
        viewModelScope.launch { handleDeleteAccount() }
    }

    fun logout() {
        disableButtons()
        viewModelScope.launch { handleLogout() }
    }

    private suspend fun getCachedUserInfo() {
        val userInfo = session.getUserInfo()
        userInfoFlow.value = cache(userInfo)
        enableButtons()
    }

    private suspend fun handleUpdateUser(updateUserInfo: UpdateUserInputModel) {
        val result = request {
            val token = session.getToken()
            service.userService.updateUser(token, updateUserInfo)
        }
        when {
            result.isFailure -> getCachedUserInfo()
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
                showToast("user information updated successfully")
            }
        }
        enableButtons()
    }

    private suspend fun handleDeleteAccount() {
        val result = request {
            val token = session.getToken()
            service.userService.deleteUser(token)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> {
                showToast("account deleted successfully")
                onSessionExpired(false)
            }
        }
    }

    private suspend fun handleLogout() {
        val result = request {
            val token = session.getToken()
            service.authService.logout(token)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> onSessionExpired(false)
        }
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