package android.epicurius.ui.screens.auth.login

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.auth.models.input.LoginInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.auth.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): AuthViewModel(service, session, context) {

    var loginEnable by mutableStateOf(true)
        private set

    fun login(
        name: String?,
        email: String?,
        password: String,
        navigateTo: () -> Unit
    ) {
        disableLogin()
        validateLoginInfo(name, email, password)
        val loginInfo = LoginInputModel(name, email, password)
        viewModelScope.launch {
            submitLogin(loginInfo, navigateTo)
        }
    }

    private suspend fun submitLogin(loginInfo: LoginInputModel, navigateTo: () -> Unit) {
        val result = request {
            service.authService.login(loginInfo)
        }
        when {
            result.isFailure -> {
                enableLogin()
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
    ): Boolean =
        when {
            name != null && !validateName(name) -> false
            email != null && !validateEmail(email) -> false
            !validatePassword(password) -> false
            else -> true
        }

    private fun enableLogin() {
        loginEnable = true
    }

    private fun disableLogin() {
        loginEnable = false
    }
}