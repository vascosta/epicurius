package android.epicurius.ui.screens.auth.login

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.auth.models.input.LoginInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.user.UserViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): UserViewModel(service, session, context) {

    fun login(
        name: String?,
        email: String?,
        password: String,
        navigateTo: () -> Unit
    ) {
        disableButtons()
        if (!validateLoginInfo(name, email, password)) {
            enableButtons()
            return
        }
        val loginInfo = LoginInputModel(name, email, password)
        viewModelScope.launch {
            handleLogin(loginInfo, navigateTo)
        }
    }

    private suspend fun handleLogin(loginInfo: LoginInputModel, navigateTo: () -> Unit) {
        val result = request {
            service.authService.login(loginInfo)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                val token = result.getTokenOrThrow()
                saveUserInfo(token)
                navigateTo()
            }
        }
    }

    private fun validateLoginInfo(
        name: String?,
        email: String?,
        password: String
    ): Boolean  {
        return when {
            name != null && !validateName(name) -> false
            email != null && !validateEmail(email) -> false
            !validatePassword(password) -> false
            else -> true
        }
    }
}